package com.postech.msproducts.config;

import com.postech.msproducts.domain.Product;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.MongoItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@EnableBatchProcessing
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
                .resource(new ClassPathResource("products.csv")) // se for passar parametros, nao usa @bean, cria metodo
                .delimited()
                .names("name", "price", "quantity_stk")
                .fieldSetMapper(fieldSetMapper)
                .build();
    }

    @Bean
    public ItemWriter<Product> itemWriter(MongoTemplate mongoTemplate) {
        MongoItemWriter<Product> writer = new MongoItemWriter<>();
        writer.setTemplate(mongoTemplate);
        writer.setCollection("products"); //https://www.linkedin.com/pulse/spring-batch-read-from-xml-write-mongo-prateek-ashtikar/
        //https://boottechnologies-ci.medium.com/spring-batch-and-mongodb-reading-and-writing-from-excel-file-fa4f55ded7b8
        // Define o nome da coleção onde os produtos serão salvos
        return writer;
    }

    @Bean
    public ItemProcessor<Product, Product> itemProcessor() {
        return new ProductProcessor();
    }
}
