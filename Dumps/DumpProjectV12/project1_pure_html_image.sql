-- MySQL dump 10.13  Distrib 8.0.21, for Win64 (x86_64)
--
-- Host: localhost    Database: project1_pure_html
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
-- Table structure for table `image`
--

DROP TABLE IF EXISTS `image`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `image` (
  `id` int NOT NULL AUTO_INCREMENT,
  `title` varchar(150) NOT NULL,
  `description` mediumtext,
  `date` date NOT NULL,
  `filePath` varchar(500) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`),
  UNIQUE KEY `filePath_UNIQUE` (`filePath`)
) ENGINE=InnoDB AUTO_INCREMENT=54 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `image`
--

LOCK TABLES `image` WRITE;
/*!40000 ALTER TABLE `image` DISABLE KEYS */;
INSERT INTO `image` VALUES (1,'Casa','è una casa','2020-08-11','/Project1_Pure_Html/ServerImages/casa.jpg'),(2,'Farfalla','A giulia piaciono','2020-08-12','/Project1_Pure_Html/ServerImages/farfalla.jpg'),(3,'Albero','è un albero','2020-08-13','/Project1_Pure_Html/ServerImages/albero.jpg'),(9,'Gatto','Tom è un gatto odioso molto cattivo,sembra una tigre','2020-08-14','/Project1_Pure_Html/ServerImages/micetto.jpg'),(10,'Delfino','il delfino è un animale curioso','2020-08-15','/Project1_Pure_Html/ServerImages/Delfino.jpg'),(11,'Cerbiatto','è un cucciolo di cerbiatto','2020-08-16','/Project1_Pure_Html/ServerImages/cerbiatto.jpg'),(12,'Fiore','è una ninfea rosa','2020-08-17','/Project1_Pure_Html/ServerImages/fiore.jpg'),(13,'Koala','il koala sta mangiando una foglia','2020-08-18','/Project1_Pure_Html/ServerImages/koala.jpg'),(14,'Roccia','è una roccia possente','2020-08-19','/Project1_Pure_Html/ServerImages/roccia.jpg'),(15,'Cavallo','un cavallo','2020-08-19','/Project1_Pure_Html/ServerImages/cavallo.jpg'),(16,'Ermellino',NULL,'2020-08-19','/Project1_Pure_Html/ServerImages/ermellino.jpg'),(17,'Gufo',NULL,'2020-08-19','/Project1_Pure_Html/ServerImages/gufo.jpg'),(18,'Piinguino',NULL,'2020-08-19','/Project1_Pure_Html/ServerImages/pinguino.jpg'),(19,'Riccio',NULL,'2020-08-13','/Project1_Pure_Html/ServerImages/riccio.jpg'),(20,'Tartaruga',NULL,'2020-08-29','/Project1_Pure_Html/ServerImages/tartaruga.jpeg'),(21,'Casa2',NULL,'2020-08-07','/Project1_Pure_Html/ServerImages/casa2.jpg'),(22,'Casa3',NULL,'2020-08-07','/Project1_Pure_Html/ServerImages/casa3.jpg'),(23,'Casa4',NULL,'2020-08-04','/Project1_Pure_Html/ServerImages/casa4.jpg'),(24,'Casa5',NULL,'2020-08-03','/Project1_Pure_Html/ServerImages/casa5.jpg'),(25,'Casa6',NULL,'2020-05-19','/Project1_Pure_Html/ServerImages/casa6.jpg'),(26,'Casa7',NULL,'2020-04-15','/Project1_Pure_Html/ServerImages/casa7.jpg'),(27,'Casa8',NULL,'2020-10-01','/Project1_Pure_Html/ServerImages/casa8.jpg'),(28,'Casa9',NULL,'2020-02-12','/Project1_Pure_Html/ServerImages/casa9.jpg'),(29,'Casa10',NULL,'2020-01-28','/Project1_Pure_Html/ServerImages/casa10.jpg'),(30,'Casa11',NULL,'2020-08-16','/Project1_Pure_Html/ServerImages/casa11.jpg'),(31,'Casa12',NULL,'2020-04-22','/Project1_Pure_Html/ServerImages/casa12.jpg'),(32,'Natura1',NULL,'2020-03-23','/Project1_Pure_Html/ServerImages/natura1.jpg'),(33,'Natura2',NULL,'2020-02-22','/Project1_Pure_Html/ServerImages/natura2.jpg'),(34,'Natura3',NULL,'2020-07-26','/Project1_Pure_Html/ServerImages/natura3.jpg'),(35,'Natura4',NULL,'2020-09-25','/Project1_Pure_Html/ServerImages/natura4.jpg'),(36,'Natura5',NULL,'2020-12-22','/Project1_Pure_Html/ServerImages/natura5.jpg'),(37,'Natura6',NULL,'2020-11-29','/Project1_Pure_Html/ServerImages/natura6.jpg'),(38,'Universo1',NULL,'2020-09-25','/Project1_Pure_Html/ServerImages/universo1.jpg'),(39,'Universo2',NULL,'2020-09-20','/Project1_Pure_Html/ServerImages/universo2.jpg'),(40,'Universo3',NULL,'2020-04-11','/Project1_Pure_Html/ServerImages/universo3.jpg'),(41,'Universo4',NULL,'2020-03-09','/Project1_Pure_Html/ServerImages/universo4.jpg'),(42,'Universo5',NULL,'2020-07-23','/Project1_Pure_Html/ServerImages/universo5.jpg'),(43,'Universo6',NULL,'2020-08-16','/Project1_Pure_Html/ServerImages/universo6.jpg'),(44,'Universo7',NULL,'2020-10-14','/Project1_Pure_Html/ServerImages/universo7.jpg'),(45,'Universo8',NULL,'2020-12-25','/Project1_Pure_Html/ServerImages/universo8.jpg'),(46,'Mare1',NULL,'2020-04-25','/Project1_Pure_Html/ServerImages/mare1.jpg'),(47,'Mare2',NULL,'2020-11-12','/Project1_Pure_Html/ServerImages/mare2.jpg'),(48,'Mare3',NULL,'2020-10-15','/Project1_Pure_Html/ServerImages/mare3.jpg'),(49,'Mare4',NULL,'2020-08-29','/Project1_Pure_Html/ServerImages/mare4.jpg'),(50,'Mare5',NULL,'2020-03-30','/Project1_Pure_Html/ServerImages/mare5.jpg'),(51,'Mare6',NULL,'2020-03-31','/Project1_Pure_Html/ServerImages/mare6.jpg'),(52,'Mare7',NULL,'2020-10-04','/Project1_Pure_Html/ServerImages/mare7.jpg'),(53,'Mare8',NULL,'2020-07-22','/Project1_Pure_Html/ServerImages/mare8.jpg');
/*!40000 ALTER TABLE `image` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2020-08-18 18:07:18
