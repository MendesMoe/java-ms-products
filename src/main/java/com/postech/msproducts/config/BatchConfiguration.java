package com.postech.msproducts.config;

import com.postech.msproducts.domain.Product;
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

@Configuration
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

        BeanWrapperFieldSetMapper<Product> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setTargetType(Product.class);

        return new FlatFileItemReaderBuilder<Product>()
                .name("productReader")
                .resource(new ClassPathResource("products-java.csv")) // se for passar parametros, nao usa @bean, cria metodo. Aqui procura dentro do resources
                .delimited() // vai so iterar na linha
                .names("name", "price", "quantity_stk")
                .fieldSetMapper(fieldSetMapper)
                .build();
    }

    @Bean
    public ItemWriter<Product> itemWriter(MongoTemplate mongoTemplate) {
        return items -> {
            for (Product item : items) {
                // procura um produto existente com o mesmo nome e preço
                Query query = new Query(Criteria.where("name").is(item.getName())
                        .andOperator(Criteria.where("price").is(item.getPrice())));
                Update update = new Update().set("quantity_stk", item.getQuantity_stk());
                FindAndModifyOptions options = new FindAndModifyOptions().returnNew(true).upsert(true);

                // update o produto existente ou insere um novo se não existir
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
