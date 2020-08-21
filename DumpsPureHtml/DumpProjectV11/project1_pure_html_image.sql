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
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `image`
--

LOCK TABLES `image` WRITE;
/*!40000 ALTER TABLE `image` DISABLE KEYS */;
INSERT INTO `image` VALUES (1,'Casa','è una casa','2020-08-11','/Project1_Pure_Html/ServerImages/casa.jpg'),(2,'Farfalla','A giulia piaciono','2020-08-12','/Project1_Pure_Html/ServerImages/farfalla.jpg'),(3,'Albero','è un albero','2020-08-13','/Project1_Pure_Html/ServerImages/albero.jpg'),(9,'Gatto','Tom è un gatto odioso molto cattivo,sembra una tigre','2020-08-14','/Project1_Pure_Html/ServerImages/micetto.jpg'),(10,'Delfino','il delfino è un animale curioso','2020-08-15','/Project1_Pure_Html/ServerImages/Delfino.jpg'),(11,'Cerbiatto','è un cucciolo di cerbiatto','2020-08-16','/Project1_Pure_Html/ServerImages/cerbiatto.jpg'),(12,'Fiore','è una ninfea rosa','2020-08-17','/Project1_Pure_Html/ServerImages/fiore.jpg'),(13,'Koala','il koala sta mangiando una foglia','2020-08-18','/Project1_Pure_Html/ServerImages/koala.jpg'),(14,'Roccia','è una roccia possente','2020-08-19','/Project1_Pure_Html/ServerImages/roccia.jpg'),(15,'Cavallo','un cavallo','2020-08-19','/Project1_Pure_Html/ServerImages/cavallo.jpg'),(16,'Ermellino',NULL,'2020-08-19','/Project1_Pure_Html/ServerImages/ermellino.jpg'),(17,'Gufo',NULL,'2020-08-19','/Project1_Pure_Html/ServerImages/gufo.jpg'),(18,'Piinguino',NULL,'2020-08-19','/Project1_Pure_Html/ServerImages/pinguino.jpg'),(19,'Riccio',NULL,'2020-08-19','/Project1_Pure_Html/ServerImages/riccio.jpg'),(20,'Tartaruga',NULL,'2020-08-19','/Project1_Pure_Html/ServerImages/tartaruga.jpeg');
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

-- Dump completed on 2020-08-18 16:43:33
