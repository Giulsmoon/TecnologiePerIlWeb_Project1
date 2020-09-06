-- MySQL dump 10.13  Distrib 8.0.21, for Win64 (x86_64)
--
-- Host: localhost    Database: project1_ria
-- ------------------------------------------------------
-- Server version	8.0.21

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `comment`
--

DROP TABLE IF EXISTS `comment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `comment` (
  `id` int NOT NULL AUTO_INCREMENT,
  `text` mediumtext NOT NULL,
  `idImage` int NOT NULL,
  `idUser` int NOT NULL,
  `date` date NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`),
  KEY `idUser_idx` (`idUser`),
  KEY `idImage_idx` (`idImage`),
  CONSTRAINT `idImage` FOREIGN KEY (`idImage`) REFERENCES `image` (`id`),
  CONSTRAINT `idUser` FOREIGN KEY (`idUser`) REFERENCES `user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=59 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `comment`
--

LOCK TABLES `comment` WRITE;
/*!40000 ALTER TABLE `comment` DISABLE KEYS */;
INSERT INTO `comment` VALUES (1,'wow che bella farfalla',2,2,'2020-10-01'),(2,'il gatto ha due grossi occhietti',9,1,'2018-12-12'),(8,'è una farfalla',2,3,'2020-03-26'),(11,'le farfalle non mi piacciono',2,1,'2020-07-04'),(12,'farfalla1',2,3,'2020-07-05'),(13,'farfalla 2',2,2,'2020-07-05'),(14,'farfalla3',2,1,'2020-07-06'),(15,'farfalla 4',2,2,'2020-07-04'),(16,'farfalla 5',2,1,'2020-07-12'),(17,'farfalla 6',2,2,'2020-07-23'),(18,'farfalla 7',2,3,'2020-07-04'),(19,'farfalla 8',2,2,'2020-07-09'),(20,'farfalla 9',2,1,'2020-07-12'),(21,'farfalla 10',2,3,'2020-07-28'),(22,'farfalla 11',2,3,'2020-07-15'),(23,'CHE BEL GATTINO',9,2,'2020-07-07'),(24,'amo i cavalli',15,5,'2020-07-14'),(25,'l\'ermellino è troppo tenero ',16,8,'2020-07-04'),(26,'Sono tenerissimi',18,1,'2020-08-17'),(27,'il koala mangia una foglia',13,1,'2020-08-17'),(28,'è un cucciolo di cerbiatto',11,1,'2020-08-17'),(29,'è un ermellino tutto bianco',16,1,'2020-08-17'),(30,'Ciao gufetto',17,2,'2020-01-01'),(31,'che bel cavallo',15,1,'2020-09-01'),(32,'mi piacerebbe vivere qui',30,1,'2020-09-01'),(33,'che bella casa ',1,1,'2020-09-01'),(37,'che bella casa',21,1,'2020-09-01'),(38,'cavallo',15,1,'2020-09-01'),(42,'ciao piccola tartarughina',20,1,'2020-09-01'),(43,'che bel paesaggio',36,1,'2020-09-01'),(45,'sono due pinguini teneri',18,1,'2020-09-01'),(47,'vorrei essere al mare',48,1,'2020-09-01'),(48,'mareee',48,1,'2020-09-01'),(49,'stupendoo',52,1,'2020-09-01'),(50,'che bel mare',52,1,'2020-09-01'),(51,'voglia d\'estateee',52,1,'2020-09-01'),(52,'casa mia',22,1,'2020-09-01'),(57,'mare stupendooo',47,1,'2020-09-01'),(58,'non mi piace l\'autunno',37,1,'2020-09-01');
/*!40000 ALTER TABLE `comment` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2020-09-07  0:21:31
