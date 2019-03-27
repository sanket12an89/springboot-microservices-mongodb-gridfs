package com.shree.web.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;

@Configuration
public class MongoGridFsTemplate extends AbstractMongoConfiguration {

	@Value("${jsa.mongo.address}")
	private String mongoAddress;

	@Value("${jsa.mongo.database}")
	private String mongoDatabase;

	@Bean
	public GridFsTemplate gridFsTemplate() throws Exception {
		return new GridFsTemplate(mongoDbFactory(), mappingMongoConverter());
	}

	@Bean
	public GridFSBucket getGridFSBuckets() {
		MongoDatabase db = mongoDbFactory().getDb();
		return GridFSBuckets.create(db);
	}

	@Override
	protected String getDatabaseName() {
		return mongoDatabase;
	}

	@Override
	public MongoClient mongoClient() {
		return new MongoClient(mongoAddress);
	}

}