package com.example.groceryapp.database

import com.mongodb.kotlin.client.coroutine.MongoClient
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import com.mongodb.MongoClientSettings
import io.ktor.server.application.Application
import org.bson.codecs.configuration.CodecRegistries
import org.bson.codecs.kotlinx.KotlinSerializerCodecProvider

import org.slf4j.LoggerFactory

object MongoDatabase {
    private val logger = LoggerFactory.getLogger(javaClass)
    private var client: MongoClient? = null
    private var mongoDatabase: MongoDatabase? = null

    fun init(application: Application) {
        val uri = application.environment.config.property("mongodb.uri").getString()
        val dbName = application.environment.config.property("mongodb.database").getString()
        
        val codecRegistry = CodecRegistries.fromRegistries(
            MongoClientSettings.getDefaultCodecRegistry(),
            CodecRegistries.fromProviders(CodecRegistries.fromProviders(KotlinSerializerCodecProvider()))
        )

        client = MongoClient.create(uri)
        mongoDatabase = client?.getDatabase(dbName)?.withCodecRegistry(codecRegistry)
        
        logger.info("Connected to MongoDB at $uri, database: $dbName with Kotlin Serialization support")
    }

    fun getDatabase(): MongoDatabase {
        return mongoDatabase ?: throw IllegalStateException("Database not initialized")
    }

    fun close() {
        client?.close()
    }
}
