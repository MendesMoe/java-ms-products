package com.postech.msproducts.config;

import com.postech.msproducts.domain.Product;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.validation.DataBinder;

import java.beans.PropertyEditorSupport;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Configuration
@Slf4j
//@EnableBatchProcessing
public class BatchConfiguration {

    @Bean
    public Job processarProductsJob(JobRepository jobRepository, Step step) {
        return new JobBuilder("import-products", jobRepository)
                .incrementer(new RunIdIncrementer()) // gera um novo id para a execução do job, instancias, melhor controle
                .start(step)
                .build();
    }

    @Bean
    public Step step(JobRepository jobRepository,
                     PlatformTransactionManager platformTransactionManager,
                     ItemReader<Product> itemReader,
                     ItemProcessor<Product, Product> itemProcessor,
                     ItemWriter<Product> itemWriter) {
        return new StepBuilder("step", jobRepository)
                .<Product, Product>chunk(20, platformTransactionManager)
                .reader(itemReader)
                .processor(itemProcessor)
                .writer(itemWriter)
                .build();
    }

    @Bean
    public ItemReader<Product> itemReader() {
        BeanWrapperFieldSetMapper<Product> fieldSetMapper = new BeanWrapperFieldSetMapper<>() {
            @Override
            public void initBinder(DataBinder binder) {
                binder.registerCustomEditor(LocalDateTime.class, new PropertyEditorSupport() {
                    @Override
                    public void setAsText(String text) throws IllegalArgumentException {
                        if (text != null && !text.isEmpty()) {
                            try {
                                setValue(LocalDateTime.parse(text, DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                            } catch (DateTimeParseException e) {
                                throw new IllegalArgumentException("Could not parse date: " + text, e);
                            }
                        } else {
                            setValue(null);
                        }
                    }
                });
            }
        };
        fieldSetMapper.setTargetType(Product.class);

        return new FlatFileItemReaderBuilder<Product>()
                .name("productReader")
                .resource(new ClassPathResource("products-java.csv"))
                .delimited()
                .names("id", "name", "description", "price", "quantity_stk", "created_at", "updated_at")
                .fieldSetMapper(fieldSetMapper)
                .build();
    }

    //Eficiência: As operações em lote são significativamente mais eficientes em termos de desempenho e uso de rede, especialmente para grandes conjuntos de dados.
    //Erro Handling: Quando você executa operações em lote, especialmente no modo desordenado (UNORDERED), uma falha em uma das operações não necessariamente impede a execução das outras.
    // Você deve considerar como quer lidar com erros em operações em lote e talvez revisar os resultados das operações para erros.
    @Bean
    public ItemWriter<Product> itemWriter(MongoTemplate mongoTemplate) {
        return items -> {
            for (Product item : items) {
                Query query = new Query(Criteria.where("id").is(item.getId()));
                // Procura um produto existente com o mesmo id
                Update update = new Update()
                        .set("name", item.getName())
                        .set("description", item.getDescription())
                        .set("price", item.getPrice())
                        .set("quantity_stk", item.getQuantity_stk());

                // Somente atualiza `updated_at` para refletir a última modificação
                update.set("updated_at", LocalDateTime.now());

                // Para novos produtos, `created_at` também é definido
                if (item.getCreated_at() == null) {
                    update.set("created_at", LocalDateTime.now());
                }
                FindAndModifyOptions options = new FindAndModifyOptions().returnNew(true).upsert(true);
                log.info("ItemWriter =>>> " + update.toString());
                // Atualiza o produto existente ou insere um novo se não existir
                mongoTemplate.findAndModify(query, update, options, Product.class);
            }
        };
    }
    /*public ItemWriter<Product> itemWriter(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<Product>()
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .dataSource(dataSource)
                .sql("INSERT INTO products (name, price, quantity_stk) VALUES (:name, :price, :quantity_stk)")
                .build();
    } utilizado para banco de dados relacionais*/

    @Bean
    public ItemProcessor<Product, Product> itemProcessor() {
        return new ProductProcessor();
    }

    @Bean
    CommandLineRunner startJob(JobLauncher jobLauncher, Job processarProductsJob) {
        return args -> {
            JobParameters jobParameters = new JobParametersBuilder()
                    .addLong("time", System.currentTimeMillis())
                    .toJobParameters();
            jobLauncher.run(processarProductsJob, jobParameters);
        };
    }
}
