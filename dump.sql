-- MySQL dump 10.13  Distrib 9.2.0, for Linux (x86_64)
--
-- Host: localhost    Database: zoo_db
-- ------------------------------------------------------
-- Server version	9.2.0

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Current Database: `zoo_db`
--

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `zoo_db` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `zoo_db`;

--
-- Table structure for table `animal_caretakers`
--

DROP TABLE IF EXISTS `animal_caretakers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `animal_caretakers` (
  `id` int NOT NULL AUTO_INCREMENT,
  `animal_id` int DEFAULT NULL,
  `staff_id` int DEFAULT NULL,
  `start_date` date NOT NULL,
  `end_date` date DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `animal_id` (`animal_id`),
  KEY `staff_id` (`staff_id`),
  CONSTRAINT `animal_caretakers_ibfk_1` FOREIGN KEY (`animal_id`) REFERENCES `animals` (`id`) ON DELETE CASCADE,
  CONSTRAINT `animal_caretakers_ibfk_2` FOREIGN KEY (`staff_id`) REFERENCES `staff` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `animal_caretakers`
--

LOCK TABLES `animal_caretakers` WRITE;
/*!40000 ALTER TABLE `animal_caretakers` DISABLE KEYS */;
INSERT INTO `animal_caretakers` VALUES (1,1,1,'2016-06-15',NULL),(2,4,1,'2013-10-01',NULL),(3,1,3,'2018-02-20',NULL),(4,8,2,'2015-04-10','2019-12-31'),(5,10,3,'2018-02-20',NULL),(6,5,6,'2020-03-01',NULL),(7,12,7,'2021-06-01','2023-12-31'),(8,14,8,'2022-04-01',NULL);
/*!40000 ALTER TABLE `animal_caretakers` ENABLE KEYS */;
UNLOCK TABLES;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`%`*/ /*!50003 TRIGGER `trg_animal_caretakers_start_date_insert` BEFORE INSERT ON `animal_caretakers` FOR EACH ROW BEGIN
    CALL check_date_not_in_future(NEW.start_date, 'Дата начала ухода не может быть в будущем');
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`%`*/ /*!50003 TRIGGER `trg_animal_caretakers_start_date_update` BEFORE UPDATE ON `animal_caretakers` FOR EACH ROW BEGIN
    CALL check_date_not_in_future(NEW.start_date, 'Дата начала ухода не может быть в будущем');
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Table structure for table `animal_diet_requirements`
--

DROP TABLE IF EXISTS `animal_diet_requirements`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `animal_diet_requirements` (
  `id` int NOT NULL AUTO_INCREMENT,
  `species_id` int DEFAULT NULL,
  `age_group` varchar(255) NOT NULL,
  `body_condition` varchar(255) NOT NULL,
  `season` varchar(255) NOT NULL,
  `feed_type_id` int DEFAULT NULL,
  `required_quantity` decimal(6,2) NOT NULL,
  `feeding_times_per_day` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `species_id` (`species_id`),
  KEY `feed_type_id` (`feed_type_id`),
  CONSTRAINT `animal_diet_requirements_ibfk_1` FOREIGN KEY (`species_id`) REFERENCES `species` (`id`) ON DELETE CASCADE,
  CONSTRAINT `animal_diet_requirements_ibfk_2` FOREIGN KEY (`feed_type_id`) REFERENCES `feed_types` (`id`) ON DELETE CASCADE,
  CONSTRAINT `animal_diet_requirements_chk_1` CHECK ((`age_group` in (_utf8mb4'Молодой',_utf8mb4'Взрослый',_utf8mb4'Старый'))),
  CONSTRAINT `animal_diet_requirements_chk_2` CHECK ((`season` in (_utf8mb4'Лето',_utf8mb4'Осень',_utf8mb4'Зима',_utf8mb4'Весна',_utf8mb4'Годовой'))),
  CONSTRAINT `animal_diet_requirements_chk_3` CHECK (((`required_quantity` > 0) and (`feeding_times_per_day` > 0)))
) ENGINE=InnoDB AUTO_INCREMENT=81 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `animal_diet_requirements`
--

LOCK TABLES `animal_diet_requirements` WRITE;
/*!40000 ALTER TABLE `animal_diet_requirements` DISABLE KEYS */;
INSERT INTO `animal_diet_requirements` VALUES (1,1,'Молодой','Хорошее','Зима',3,6.00,3),(2,1,'Молодой','Хорошее','Лето',3,5.50,3),(3,1,'Молодой','Плохое','Зима',3,5.00,2),(4,1,'Молодой','Плохое','Лето',3,4.50,2),(5,1,'Молодой','Хорошее','Весна',3,5.00,3),(6,1,'Молодой','Хорошее','Осень',3,4.50,3),(7,1,'Молодой','Плохое','Весна',3,4.00,2),(8,1,'Молодой','Плохое','Осень',3,3.50,2),(9,1,'Взрослый','Хорошее','Зима',3,10.00,3),(10,1,'Взрослый','Хорошее','Лето',3,9.00,3),(11,1,'Взрослый','Плохое','Зима',3,8.00,2),(12,1,'Взрослый','Плохое','Лето',3,7.00,2),(13,1,'Взрослый','Хорошее','Весна',3,9.00,3),(14,1,'Взрослый','Хорошее','Осень',3,8.00,3),(15,1,'Взрослый','Плохое','Весна',3,7.00,2),(16,1,'Взрослый','Плохое','Осень',3,6.00,2),(17,2,'Молодой','Хорошее','Зима',1,4.00,3),(18,2,'Молодой','Хорошее','Лето',1,5.00,4),(19,2,'Молодой','Плохое','Зима',1,3.00,3),(20,2,'Молодой','Плохое','Лето',1,4.00,4),(21,2,'Молодой','Хорошее','Весна',1,3.00,3),(22,2,'Молодой','Хорошее','Осень',1,4.00,4),(23,2,'Молодой','Плохое','Весна',1,2.00,3),(24,2,'Молодой','Плохое','Осень',1,3.00,4),(25,2,'Взрослый','Хорошее','Зима',1,6.00,3),(26,2,'Взрослый','Хорошее','Лето',1,7.00,4),(27,2,'Взрослый','Плохое','Зима',1,5.00,3),(28,2,'Взрослый','Плохое','Лето',1,6.00,4),(29,2,'Взрослый','Хорошее','Весна',1,5.00,3),(30,2,'Взрослый','Хорошее','Осень',1,6.00,4),(31,2,'Взрослый','Плохое','Весна',1,4.00,3),(32,2,'Взрослый','Плохое','Осень',1,5.00,4),(33,3,'Молодой','Хорошее','Годовой',1,3.00,3),(34,3,'Молодой','Плохое','Годовой',1,2.00,2),(35,3,'Взрослый','Хорошее','Годовой',1,4.00,3),(36,3,'Взрослый','Плохое','Годовой',1,3.00,2),(37,4,'Молодой','Хорошее','Зима',3,5.00,3),(38,4,'Молодой','Хорошее','Лето',3,4.00,3),(39,4,'Молодой','Плохое','Зима',3,3.50,2),(40,4,'Молодой','Плохое','Лето',3,3.00,2),(41,4,'Молодой','Хорошее','Весна',3,4.00,3),(42,4,'Молодой','Хорошее','Осень',3,3.00,3),(43,4,'Молодой','Плохое','Весна',3,2.50,2),(44,4,'Молодой','Плохое','Осень',3,2.00,2),(45,4,'Взрослый','Хорошее','Зима',3,8.00,3),(46,4,'Взрослый','Хорошее','Лето',3,7.00,3),(47,4,'Взрослый','Плохое','Зима',3,6.00,2),(48,4,'Взрослый','Плохое','Лето',3,5.50,2),(49,4,'Взрослый','Хорошее','Весна',3,7.00,3),(50,4,'Взрослый','Хорошее','Осень',3,6.00,3),(51,4,'Взрослый','Плохое','Весна',3,5.00,2),(52,4,'Взрослый','Плохое','Осень',3,4.50,2),(53,5,'Молодой','Хорошее','Годовой',3,5.00,3),(54,5,'Молодой','Плохое','Годовой',3,4.00,2),(55,5,'Взрослый','Хорошее','Годовой',3,9.00,3),(56,5,'Взрослый','Плохое','Годовой',3,7.00,2),(57,6,'Молодой','Хорошее','Зима',4,5.00,3),(58,6,'Молодой','Хорошее','Лето',1,4.00,3),(59,6,'Молодой','Плохое','Зима',4,3.50,2),(60,6,'Молодой','Плохое','Лето',1,3.00,2),(61,6,'Молодой','Хорошее','Весна',1,4.00,3),(62,6,'Молодой','Хорошее','Осень',4,3.00,3),(63,6,'Молодой','Плохое','Весна',1,2.50,2),(64,6,'Молодой','Плохое','Осень',4,2.00,2),(65,6,'Взрослый','Хорошее','Зима',4,8.00,3),(66,6,'Взрослый','Хорошее','Лето',1,7.00,3),(67,6,'Взрослый','Плохое','Зима',4,6.00,2),(68,6,'Взрослый','Плохое','Лето',1,5.50,2),(69,6,'Взрослый','Хорошее','Весна',1,7.00,3),(70,6,'Взрослый','Хорошее','Осень',4,6.00,3),(71,6,'Взрослый','Плохое','Весна',1,5.00,2),(72,6,'Взрослый','Плохое','Осень',4,4.50,2),(73,7,'Молодой','Хорошее','Годовой',1,4.00,3),(74,7,'Молодой','Плохое','Годовой',1,3.00,2),(75,7,'Взрослый','Хорошее','Годовой',1,6.00,3),(76,7,'Взрослый','Плохое','Годовой',1,5.00,2),(77,8,'Молодой','Хорошее','Годовой',2,4.00,3),(78,8,'Молодой','Плохое','Годовой',2,3.00,2),(79,8,'Взрослый','Хорошее','Годовой',2,6.00,3),(80,8,'Взрослый','Плохое','Годовой',2,5.00,2);
/*!40000 ALTER TABLE `animal_diet_requirements` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `animal_diseases`
--

DROP TABLE IF EXISTS `animal_diseases`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `animal_diseases` (
  `id` int NOT NULL AUTO_INCREMENT,
  `animal_id` int DEFAULT NULL,
  `veterinarian_id` int DEFAULT NULL,
  `disease_id` int DEFAULT NULL,
  `diagnosed_date` date NOT NULL,
  `recovery_date` date DEFAULT NULL,
  `notes` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `animal_id` (`animal_id`),
  KEY `veterinarian_id` (`veterinarian_id`),
  KEY `disease_id` (`disease_id`),
  CONSTRAINT `animal_diseases_ibfk_1` FOREIGN KEY (`animal_id`) REFERENCES `animals` (`id`) ON DELETE CASCADE,
  CONSTRAINT `animal_diseases_ibfk_2` FOREIGN KEY (`veterinarian_id`) REFERENCES `staff` (`id`) ON DELETE CASCADE,
  CONSTRAINT `animal_diseases_ibfk_3` FOREIGN KEY (`disease_id`) REFERENCES `diseases` (`id`) ON DELETE CASCADE,
  CONSTRAINT `animal_diseases_chk_1` CHECK (((`recovery_date` is null) or (`recovery_date` >= `diagnosed_date`)))
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `animal_diseases`
--

LOCK TABLES `animal_diseases` WRITE;
/*!40000 ALTER TABLE `animal_diseases` DISABLE KEYS */;
INSERT INTO `animal_diseases` VALUES (1,1,1,1,'2017-06-15','2017-07-01','Легкое течение'),(2,4,1,1,'2016-09-10','2016-09-25','Умеренное течение'),(3,4,1,2,'2018-01-15','2018-01-30','Назначено противореспираторное лечение');
/*!40000 ALTER TABLE `animal_diseases` ENABLE KEYS */;
UNLOCK TABLES;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`%`*/ /*!50003 TRIGGER `trg_animal_diseases_veterinarian_insert` BEFORE INSERT ON `animal_diseases` FOR EACH ROW BEGIN
    DECLARE cat_id INT;
    DECLARE cnt INT;
    SELECT category_id INTO cat_id FROM staff WHERE id = NEW.veterinarian_id;
    IF cat_id <> 1 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Сотрудник не является ветеринаром';
    END IF;
    SELECT COUNT(*) INTO cnt FROM animal_caretakers
    WHERE animal_id = NEW.animal_id AND staff_id = NEW.veterinarian_id
          AND (end_date IS NULL OR end_date >= NEW.diagnosed_date);
    IF cnt = 0 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Ветеринар не ухаживает за этим животным';
    END IF;
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`%`*/ /*!50003 TRIGGER `trg_animal_diseases_diagnosed_date_insert` BEFORE INSERT ON `animal_diseases` FOR EACH ROW BEGIN
    CALL check_date_not_in_future(NEW.diagnosed_date, 'Дата диагноза не может быть в будущем');
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`%`*/ /*!50003 TRIGGER `trg_animal_diseases_veterinarian_update` BEFORE UPDATE ON `animal_diseases` FOR EACH ROW BEGIN
    DECLARE cat_id INT;
    DECLARE cnt INT;
    SELECT category_id INTO cat_id FROM staff WHERE id = NEW.veterinarian_id;
    IF cat_id <> 1 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Сотрудник не является ветеринаром';
    END IF;
    SELECT COUNT(*) INTO cnt FROM animal_caretakers
    WHERE animal_id = NEW.animal_id AND staff_id = NEW.veterinarian_id
          AND (end_date IS NULL OR end_date >= NEW.diagnosed_date);
    IF cnt = 0 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Ветеринар не ухаживает за этим животным';
    END IF;
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`%`*/ /*!50003 TRIGGER `trg_animal_diseases_diagnosed_date_update` BEFORE UPDATE ON `animal_diseases` FOR EACH ROW BEGIN
    CALL check_date_not_in_future(NEW.diagnosed_date, 'Дата диагноза не может быть в будущем');
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Table structure for table `animal_medical_records`
--

DROP TABLE IF EXISTS `animal_medical_records`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `animal_medical_records` (
  `id` int NOT NULL AUTO_INCREMENT,
  `animal_id` int DEFAULT NULL,
  `record_date` date NOT NULL,
  `weight` decimal(6,2) DEFAULT NULL,
  `height` decimal(6,2) DEFAULT NULL,
  `notes` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `animal_id` (`animal_id`),
  CONSTRAINT `animal_medical_records_ibfk_1` FOREIGN KEY (`animal_id`) REFERENCES `animals` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `animal_medical_records`
--

LOCK TABLES `animal_medical_records` WRITE;
/*!40000 ALTER TABLE `animal_medical_records` DISABLE KEYS */;
INSERT INTO `animal_medical_records` VALUES (1,1,'2018-06-15',220.50,1.20,'Регулярный осмотр'),(2,4,'2017-04-10',190.00,1.10,'Стабильное состояние'),(3,2,'2023-05-20',180.00,1.15,'Плановый осмотр'),(4,5,'2023-06-10',170.50,1.05,'Профилактический осмотр');
/*!40000 ALTER TABLE `animal_medical_records` ENABLE KEYS */;
UNLOCK TABLES;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`%`*/ /*!50003 TRIGGER `trg_animal_medical_records_record_date_insert` BEFORE INSERT ON `animal_medical_records` FOR EACH ROW BEGIN
    CALL check_date_not_in_future(NEW.record_date, 'Дата медосмотра не может быть в будущем');
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`%`*/ /*!50003 TRIGGER `trg_animal_medical_records_record_date_update` BEFORE UPDATE ON `animal_medical_records` FOR EACH ROW BEGIN
    CALL check_date_not_in_future(NEW.record_date, 'Дата медосмотра не может быть в будущем');
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Table structure for table `animal_movement_history`
--

DROP TABLE IF EXISTS `animal_movement_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `animal_movement_history` (
  `id` int NOT NULL AUTO_INCREMENT,
  `animal_id` int DEFAULT NULL,
  `from_enclosure` int DEFAULT NULL,
  `to_enclosure` int DEFAULT NULL,
  `move_date` date NOT NULL,
  PRIMARY KEY (`id`),
  KEY `animal_id` (`animal_id`),
  KEY `from_enclosure` (`from_enclosure`),
  KEY `to_enclosure` (`to_enclosure`),
  CONSTRAINT `animal_movement_history_ibfk_1` FOREIGN KEY (`animal_id`) REFERENCES `animals` (`id`) ON DELETE CASCADE,
  CONSTRAINT `animal_movement_history_ibfk_2` FOREIGN KEY (`from_enclosure`) REFERENCES `enclosures` (`id`) ON DELETE CASCADE,
  CONSTRAINT `animal_movement_history_ibfk_3` FOREIGN KEY (`to_enclosure`) REFERENCES `enclosures` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `animal_movement_history`
--

LOCK TABLES `animal_movement_history` WRITE;
/*!40000 ALTER TABLE `animal_movement_history` DISABLE KEYS */;
INSERT INTO `animal_movement_history` VALUES (1,1,1,9,'2018-07-10'),(2,6,3,10,'2019-05-05'),(3,15,7,9,'2023-09-10'),(4,16,8,10,'2023-10-01');
/*!40000 ALTER TABLE `animal_movement_history` ENABLE KEYS */;
UNLOCK TABLES;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`%`*/ /*!50003 TRIGGER `trg_animal_movement_history_move_date_insert` BEFORE INSERT ON `animal_movement_history` FOR EACH ROW BEGIN
    CALL check_date_not_in_future(NEW.move_date, 'Дата перемещения не может быть в будущем');
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`%`*/ /*!50003 TRIGGER `trg_animal_movement_update_enclosure_insert` AFTER INSERT ON `animal_movement_history` FOR EACH ROW BEGIN
    DECLARE latest_movement_date DATE;
    SELECT MAX(move_date) INTO latest_movement_date
    FROM animal_movement_history
    WHERE animal_id = NEW.animal_id;
    
    IF NEW.move_date = latest_movement_date THEN
        UPDATE animals
        SET enclosure_id = NEW.to_enclosure
        WHERE id = NEW.animal_id;
    END IF;
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`%`*/ /*!50003 TRIGGER `trg_animal_movement_history_move_date_update` BEFORE UPDATE ON `animal_movement_history` FOR EACH ROW BEGIN
    CALL check_date_not_in_future(NEW.move_date, 'Дата перемещения не может быть в будущем');
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`%`*/ /*!50003 TRIGGER `trg_animal_movement_update_enclosure_update` AFTER UPDATE ON `animal_movement_history` FOR EACH ROW BEGIN
    DECLARE latest_movement_date DATE;
    SELECT MAX(move_date) INTO latest_movement_date
    FROM animal_movement_history
    WHERE animal_id = NEW.animal_id;
    
    IF NEW.move_date = latest_movement_date THEN
        UPDATE animals
        SET enclosure_id = NEW.to_enclosure
        WHERE id = NEW.animal_id;
    END IF;
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Table structure for table `animal_vaccinations`
--

DROP TABLE IF EXISTS `animal_vaccinations`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `animal_vaccinations` (
  `id` int NOT NULL AUTO_INCREMENT,
  `animal_id` int DEFAULT NULL,
  `vaccine_id` int DEFAULT NULL,
  `vaccination_date` date NOT NULL,
  `next_vaccination_date` date DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `animal_id` (`animal_id`),
  KEY `vaccine_id` (`vaccine_id`),
  CONSTRAINT `animal_vaccinations_ibfk_1` FOREIGN KEY (`animal_id`) REFERENCES `animals` (`id`) ON DELETE CASCADE,
  CONSTRAINT `animal_vaccinations_ibfk_2` FOREIGN KEY (`vaccine_id`) REFERENCES `vaccines` (`id`) ON DELETE CASCADE,
  CONSTRAINT `animal_vaccinations_chk_1` CHECK (((`next_vaccination_date` is null) or (`next_vaccination_date` >= `vaccination_date`)))
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `animal_vaccinations`
--

LOCK TABLES `animal_vaccinations` WRITE;
/*!40000 ALTER TABLE `animal_vaccinations` DISABLE KEYS */;
INSERT INTO `animal_vaccinations` VALUES (1,1,1,'2017-06-15','2018-06-15'),(2,4,1,'2016-09-10','2017-09-10'),(3,4,2,'2018-02-01','2019-02-01');
/*!40000 ALTER TABLE `animal_vaccinations` ENABLE KEYS */;
UNLOCK TABLES;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`%`*/ /*!50003 TRIGGER `trg_animal_vaccinations_vaccination_date_insert` BEFORE INSERT ON `animal_vaccinations` FOR EACH ROW BEGIN
    CALL check_date_not_in_future(NEW.vaccination_date, 'Дата вакцинации не может быть в будущем');
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`%`*/ /*!50003 TRIGGER `trg_animal_vaccinations_vaccination_date_update` BEFORE UPDATE ON `animal_vaccinations` FOR EACH ROW BEGIN
    CALL check_date_not_in_future(NEW.vaccination_date, 'Дата вакцинации не может быть в будущем');
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Table structure for table `animals`
--

DROP TABLE IF EXISTS `animals`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `animals` (
  `id` int NOT NULL AUTO_INCREMENT,
  `species_id` int DEFAULT NULL,
  `name` varchar(255) NOT NULL,
  `gender` char(1) DEFAULT NULL,
  `birth_date` date NOT NULL,
  `arrival_date` date NOT NULL,
  `enclosure_id` int DEFAULT NULL,
  `parent1_id` int DEFAULT NULL,
  `parent2_id` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `species_id` (`species_id`),
  KEY `enclosure_id` (`enclosure_id`),
  KEY `parent1_id` (`parent1_id`),
  KEY `parent2_id` (`parent2_id`),
  CONSTRAINT `animals_ibfk_1` FOREIGN KEY (`species_id`) REFERENCES `species` (`id`) ON DELETE CASCADE,
  CONSTRAINT `animals_ibfk_2` FOREIGN KEY (`enclosure_id`) REFERENCES `enclosures` (`id`) ON DELETE CASCADE,
  CONSTRAINT `animals_ibfk_3` FOREIGN KEY (`parent1_id`) REFERENCES `animals` (`id`) ON DELETE CASCADE,
  CONSTRAINT `animals_ibfk_4` FOREIGN KEY (`parent2_id`) REFERENCES `animals` (`id`) ON DELETE CASCADE,
  CONSTRAINT `animals_chk_1` CHECK ((`gender` in (_utf8mb4'М',_utf8mb4'Ж'))),
  CONSTRAINT `animals_chk_2` CHECK ((`arrival_date` >= `birth_date`))
) ENGINE=InnoDB AUTO_INCREMENT=1007 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `animals`
--

LOCK TABLES `animals` WRITE;
/*!40000 ALTER TABLE `animals` DISABLE KEYS */;
INSERT INTO `animals` VALUES (1,1,'Тигр Рой','М','2014-04-10','2015-05-01',1,NULL,NULL),(2,1,'Тигрица Лана','Ж','2013-03-12','2014-04-20',1,NULL,NULL),(3,1,'Тигрёнок Симба','М','2023-05-15','2023-06-01',1,1,2),(4,2,'Зебра Зара','Ж','2016-07-22','2017-08-10',4,NULL,NULL),(5,2,'Зебра Барни','М','2015-06-18','2016-07-05',4,NULL,NULL),(6,2,'Зебрёнок Макс','М','2020-04-10','2020-04-15',4,4,5),(7,3,'Мартышка Лида','Ж','2017-08-01','2018-08-25',5,NULL,NULL),(8,3,'Мартышка Адам','М','2016-10-10','2017-11-05',5,NULL,NULL),(9,3,'Мартышка Игорь','М','2021-02-11','2021-02-20',5,7,8),(10,4,'Волчица Альфа','Ж','2014-11-20','2015-12-01',3,NULL,NULL),(11,4,'Волк Брут','М','2013-10-18','2014-11-10',3,NULL,NULL),(12,4,'Волчонок Гром','М','2019-01-03','2019-01-15',3,10,11),(13,5,'Львица Кира','Ж','2015-06-25','2016-07-10',2,NULL,NULL),(14,5,'Лев Рамзес','М','2014-05-14','2015-06-20',2,NULL,NULL),(15,5,'Львёнок Лео','М','2020-09-10','2020-09-20',2,13,14),(16,6,'Панда Лили','Ж','2016-01-17','2017-02-25',6,NULL,NULL),(17,6,'Панда Влад','М','2015-02-20','2016-03-10',6,NULL,NULL),(18,6,'Панда Снежок','М','2022-03-01','2022-03-15',6,16,17),(19,7,'Кенгуру Кенни','М','2018-09-14','2019-10-01',7,NULL,NULL),(20,7,'Кенгуру Мими','Ж','2019-01-05','2020-02-01',7,NULL,NULL),(21,7,'Кенгурёнок Джек','М','2023-06-18','2023-06-25',7,19,20),(22,2,'Зебрёнок Ольга','Ж','2023-08-10','2023-08-15',4,4,5),(23,8,'Пингвин Квайла','М','2018-02-02','2018-03-01',8,NULL,NULL),(24,8,'Пингвин Лола','Ж','2019-05-05','2019-06-01',8,NULL,NULL);
/*!40000 ALTER TABLE `animals` ENABLE KEYS */;
UNLOCK TABLES;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`%`*/ /*!50003 TRIGGER `trg_animals_incompatible_species_insert` BEFORE INSERT ON `animals` FOR EACH ROW BEGIN
    CALL check_animals_compatibility(NEW.species_id, NEW.enclosure_id);
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`%`*/ /*!50003 TRIGGER `trg_animals_need_warm_insert` BEFORE INSERT ON `animals` FOR EACH ROW BEGIN
    DECLARE need_warm CHAR(1);
    DECLARE is_warm CHAR(1);
    DECLARE cur_month INT;
    SET cur_month = MONTH(CURDATE());
    SELECT s.need_warm INTO need_warm FROM species s WHERE s.id = NEW.species_id;
    SELECT e.is_warm INTO is_warm FROM enclosures e WHERE e.id = NEW.enclosure_id;
    IF need_warm = 'Y' AND is_warm <> 'Y' AND (cur_month = 12 OR cur_month = 1 OR cur_month = 2) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Зимой животное требует отапливаемого помещения';
    END IF;
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`%`*/ /*!50003 TRIGGER `trg_animals_birth_date_insert` BEFORE INSERT ON `animals` FOR EACH ROW BEGIN
    CALL check_date_not_in_future(NEW.birth_date, 'Дата рождения животного не может быть в будущем');
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`%`*/ /*!50003 TRIGGER `trg_animals_arrival_date_insert` BEFORE INSERT ON `animals` FOR EACH ROW BEGIN
    CALL check_date_not_in_future(NEW.arrival_date, 'Дата поступления животного не может быть в будущем');
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`%`*/ /*!50003 TRIGGER `trg_animals_incompatible_species_update` BEFORE UPDATE ON `animals` FOR EACH ROW BEGIN
    CALL check_animals_compatibility(NEW.species_id, NEW.enclosure_id);
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`%`*/ /*!50003 TRIGGER `trg_animals_need_warm_update` BEFORE UPDATE ON `animals` FOR EACH ROW BEGIN
    DECLARE need_warm CHAR(1);
    DECLARE is_warm CHAR(1);
    DECLARE cur_month INT;
    SET cur_month = MONTH(CURDATE());
    SELECT s.need_warm INTO need_warm FROM species s WHERE s.id = NEW.species_id;
    SELECT e.is_warm INTO is_warm FROM enclosures e WHERE e.id = NEW.enclosure_id;
    IF need_warm = 'Y' AND is_warm <> 'Y' AND (cur_month = 12 OR cur_month = 1 OR cur_month = 2) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Зимой животное требует отапливаемого помещения';
    END IF;
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`%`*/ /*!50003 TRIGGER `trg_animals_birth_date_update` BEFORE UPDATE ON `animals` FOR EACH ROW BEGIN
    CALL check_date_not_in_future(NEW.birth_date, 'Дата рождения животного не может быть в будущем');
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`%`*/ /*!50003 TRIGGER `trg_animals_arrival_date_update` BEFORE UPDATE ON `animals` FOR EACH ROW BEGIN
    CALL check_date_not_in_future(NEW.arrival_date, 'Дата поступления животного не может быть в будущем');
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Table structure for table `category_attributes`
--

DROP TABLE IF EXISTS `category_attributes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `category_attributes` (
  `id` int NOT NULL AUTO_INCREMENT,
  `category_id` int DEFAULT NULL,
  `attribute_name` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_attribute` (`category_id`,`attribute_name`),
  CONSTRAINT `category_attributes_ibfk_1` FOREIGN KEY (`category_id`) REFERENCES `staff_categories` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `category_attributes`
--

LOCK TABLES `category_attributes` WRITE;
/*!40000 ALTER TABLE `category_attributes` DISABLE KEYS */;
INSERT INTO `category_attributes` VALUES (2,1,'Опыт работы (лет)'),(1,1,'Специализация'),(3,2,'Смена'),(4,2,'Уровень ответственности'),(6,3,'Метод дрессировки'),(5,3,'Тип животных'),(7,4,'Профессия'),(8,4,'Сертификаты'),(9,5,'Должность'),(10,5,'Отдел');
/*!40000 ALTER TABLE `category_attributes` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `climate_zones`
--

DROP TABLE IF EXISTS `climate_zones`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `climate_zones` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`),
  CONSTRAINT `climate_zones_chk_1` CHECK ((`name` in (_utf8mb4'Тропический',_utf8mb4'Субтропический',_utf8mb4'Умеренный',_utf8mb4'Континентальный',_utf8mb4'Арктический',_utf8mb4'Средиземноморский')))
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `climate_zones`
--

LOCK TABLES `climate_zones` WRITE;
/*!40000 ALTER TABLE `climate_zones` DISABLE KEYS */;
INSERT INTO `climate_zones` VALUES (5,'Арктический'),(4,'Континентальный'),(6,'Средиземноморский'),(2,'Субтропический'),(1,'Тропический'),(3,'Умеренный');
/*!40000 ALTER TABLE `climate_zones` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `daily_feeding_menu`
--

DROP TABLE IF EXISTS `daily_feeding_menu`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `daily_feeding_menu` (
  `id` int NOT NULL AUTO_INCREMENT,
  `animal_id` int DEFAULT NULL,
  `diet_id` int DEFAULT NULL,
  `feeding_number` int NOT NULL,
  `feeding_date_time` timestamp NOT NULL,
  `feed_item_id` int DEFAULT NULL,
  `quantity` decimal(6,2) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `animal_id` (`animal_id`),
  KEY `diet_id` (`diet_id`),
  KEY `feed_item_id` (`feed_item_id`),
  CONSTRAINT `daily_feeding_menu_ibfk_1` FOREIGN KEY (`animal_id`) REFERENCES `animals` (`id`) ON DELETE CASCADE,
  CONSTRAINT `daily_feeding_menu_ibfk_2` FOREIGN KEY (`diet_id`) REFERENCES `animal_diet_requirements` (`id`) ON DELETE CASCADE,
  CONSTRAINT `daily_feeding_menu_ibfk_3` FOREIGN KEY (`feed_item_id`) REFERENCES `feed_items` (`id`) ON DELETE CASCADE,
  CONSTRAINT `daily_feeding_menu_chk_1` CHECK ((`feeding_number` > 0))
) ENGINE=InnoDB AUTO_INCREMENT=73 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `daily_feeding_menu`
--

LOCK TABLES `daily_feeding_menu` WRITE;
/*!40000 ALTER TABLE `daily_feeding_menu` DISABLE KEYS */;
INSERT INTO `daily_feeding_menu` VALUES (1,1,13,1,'2025-04-23 08:00:00',7,3.00),(2,1,13,2,'2025-04-23 12:00:00',7,3.00),(3,1,13,3,'2025-04-23 18:00:00',7,3.00),(4,2,13,1,'2025-04-23 08:00:00',7,3.00),(5,2,13,2,'2025-04-23 12:00:00',7,3.00),(6,2,13,3,'2025-04-23 18:00:00',7,3.00),(7,3,5,1,'2025-04-23 08:00:00',7,1.75),(8,3,5,2,'2025-04-23 12:00:00',7,1.75),(9,3,5,3,'2025-04-23 18:00:00',7,1.50),(10,4,29,1,'2025-04-23 08:00:00',1,1.67),(11,4,29,2,'2025-04-23 12:00:00',1,1.67),(12,4,29,3,'2025-04-23 18:00:00',1,1.67),(13,5,29,1,'2025-04-23 08:00:00',1,1.67),(14,5,29,2,'2025-04-23 12:00:00',1,1.67),(15,5,29,3,'2025-04-23 18:00:00',1,1.67),(16,6,29,1,'2025-04-23 08:00:00',1,1.67),(17,6,29,2,'2025-04-23 12:00:00',1,1.67),(18,6,29,3,'2025-04-23 18:00:00',1,1.67),(19,22,21,1,'2025-04-23 08:00:00',1,1.00),(20,22,21,2,'2025-04-23 12:00:00',1,1.00),(21,22,21,3,'2025-04-23 18:00:00',1,1.00),(22,7,35,1,'2025-04-23 08:00:00',2,1.33),(23,7,35,2,'2025-04-23 12:00:00',2,1.33),(24,7,35,3,'2025-04-23 18:00:00',2,1.33),(25,8,35,1,'2025-04-23 08:00:00',2,1.33),(26,8,35,2,'2025-04-23 12:00:00',2,1.33),(27,8,35,3,'2025-04-23 18:00:00',2,1.33),(28,9,33,1,'2025-04-23 08:00:00',2,1.00),(29,9,33,2,'2025-04-23 12:00:00',2,1.00),(30,9,33,3,'2025-04-23 18:00:00',2,1.00),(31,10,49,1,'2025-04-23 08:00:00',7,2.33),(32,10,49,2,'2025-04-23 12:00:00',7,2.33),(33,10,49,3,'2025-04-23 18:00:00',7,2.33),(34,11,49,1,'2025-04-23 08:00:00',7,2.33),(35,11,49,2,'2025-04-23 12:00:00',7,2.33),(36,11,49,3,'2025-04-23 18:00:00',7,2.33),(37,12,49,1,'2025-04-23 08:00:00',7,2.33),(38,12,49,2,'2025-04-23 12:00:00',7,2.33),(39,12,49,3,'2025-04-23 18:00:00',7,2.33),(40,13,55,1,'2025-04-23 08:00:00',7,3.00),(41,13,55,2,'2025-04-23 12:00:00',7,3.00),(42,13,55,3,'2025-04-23 18:00:00',7,3.00),(43,14,55,1,'2025-04-23 08:00:00',7,3.00),(44,14,55,2,'2025-04-23 12:00:00',7,3.00),(45,14,55,3,'2025-04-23 18:00:00',7,3.00),(46,15,55,1,'2025-04-23 08:00:00',7,3.00),(47,15,55,2,'2025-04-23 12:00:00',7,3.00),(48,15,55,3,'2025-04-23 18:00:00',7,3.00),(49,16,69,1,'2025-04-23 08:00:00',1,2.33),(50,16,69,2,'2025-04-23 12:00:00',1,2.33),(51,16,69,3,'2025-04-23 18:00:00',1,2.33),(52,17,69,1,'2025-04-23 08:00:00',1,2.33),(53,17,69,2,'2025-04-23 12:00:00',1,2.33),(54,17,69,3,'2025-04-23 18:00:00',1,2.33),(55,18,61,1,'2025-04-23 08:00:00',1,1.33),(56,18,61,2,'2025-04-23 12:00:00',1,1.33),(57,18,61,3,'2025-04-23 18:00:00',1,1.33),(58,19,75,1,'2025-04-23 08:00:00',1,2.00),(59,19,75,2,'2025-04-23 12:00:00',1,2.00),(60,19,75,3,'2025-04-23 18:00:00',1,2.00),(61,20,75,1,'2025-04-23 08:00:00',1,2.00),(62,20,75,2,'2025-04-23 12:00:00',1,2.00),(63,20,75,3,'2025-04-23 18:00:00',1,2.00),(64,21,73,1,'2025-04-23 08:00:00',1,1.33),(65,21,73,2,'2025-04-23 12:00:00',1,1.33),(66,21,73,3,'2025-04-23 18:00:00',1,1.33),(67,23,79,1,'2025-04-23 08:00:00',6,2.00),(68,23,79,2,'2025-04-23 12:00:00',6,2.00),(69,23,79,3,'2025-04-23 18:00:00',6,2.00),(70,24,79,1,'2025-04-23 08:00:00',6,2.00),(71,24,79,2,'2025-04-23 12:00:00',6,2.00),(72,24,79,3,'2025-04-23 18:00:00',6,2.00);
/*!40000 ALTER TABLE `daily_feeding_menu` ENABLE KEYS */;
UNLOCK TABLES;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`%`*/ /*!50003 TRIGGER `trg_daily_feeding_menu_feeding_date_time_insert` BEFORE INSERT ON `daily_feeding_menu` FOR EACH ROW BEGIN
    CALL check_date_not_in_future(NEW.feeding_date_time, 'Дата кормления не может быть в будущем');
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`%`*/ /*!50003 TRIGGER `trg_feed_inventory_after_feeding_insert` AFTER INSERT ON `daily_feeding_menu` FOR EACH ROW BEGIN
    DECLARE feed_item_id INT;
    SELECT fi.id INTO feed_item_id
    FROM feed_items fi
    WHERE fi.id = NEW.feed_item_id;
    
    IF EXISTS (
        SELECT 1 
        FROM feed_inventory 
        WHERE feed_item_id = feed_item_id 
        AND quantity >= NEW.quantity
    ) THEN
        UPDATE feed_inventory
        SET quantity = quantity - NEW.quantity
        WHERE feed_item_id = feed_item_id;
    ELSE
        SIGNAL SQLSTATE '45000' 
        SET MESSAGE_TEXT = 'Недостаточное количество корма на складе';
    END IF;
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`%`*/ /*!50003 TRIGGER `trg_daily_feeding_menu_feeding_date_time_update` BEFORE UPDATE ON `daily_feeding_menu` FOR EACH ROW BEGIN
    CALL check_date_not_in_future(NEW.feeding_date_time, 'Дата кормления не может быть в будущем');
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`%`*/ /*!50003 TRIGGER `trg_feed_inventory_after_feeding_update` AFTER UPDATE ON `daily_feeding_menu` FOR EACH ROW BEGIN
    DECLARE feed_item_id INT;
    SELECT fi.id INTO feed_item_id
    FROM feed_items fi
    WHERE fi.id = NEW.feed_item_id;
    
    IF NEW.quantity != OLD.quantity THEN
        UPDATE feed_inventory
        SET quantity = quantity + OLD.quantity
        WHERE feed_item_id = feed_item_id;
        
        IF EXISTS (
            SELECT 1 
            FROM feed_inventory 
            WHERE feed_item_id = feed_item_id 
            AND quantity >= NEW.quantity
        ) THEN
            UPDATE feed_inventory
            SET quantity = quantity - NEW.quantity
            WHERE feed_item_id = feed_item_id;
        ELSE
            SIGNAL SQLSTATE '45000' 
            SET MESSAGE_TEXT = 'Недостаточное количество корма на складе';
        END IF;
    END IF;
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Table structure for table `diseases`
--

DROP TABLE IF EXISTS `diseases`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `diseases` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `diseases`
--

LOCK TABLES `diseases` WRITE;
/*!40000 ALTER TABLE `diseases` DISABLE KEYS */;
INSERT INTO `diseases` VALUES (1,'Паразитарная инфекция'),(2,'Респираторная инфекция');
/*!40000 ALTER TABLE `diseases` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `enclosure_neighbors`
--

DROP TABLE IF EXISTS `enclosure_neighbors`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `enclosure_neighbors` (
  `enclosure1_id` int NOT NULL,
  `enclosure2_id` int NOT NULL,
  PRIMARY KEY (`enclosure1_id`,`enclosure2_id`),
  KEY `enclosure2_id` (`enclosure2_id`),
  CONSTRAINT `enclosure_neighbors_ibfk_1` FOREIGN KEY (`enclosure1_id`) REFERENCES `enclosures` (`id`) ON DELETE CASCADE,
  CONSTRAINT `enclosure_neighbors_ibfk_2` FOREIGN KEY (`enclosure2_id`) REFERENCES `enclosures` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `enclosure_neighbors`
--

LOCK TABLES `enclosure_neighbors` WRITE;
/*!40000 ALTER TABLE `enclosure_neighbors` DISABLE KEYS */;
INSERT INTO `enclosure_neighbors` VALUES (4,5),(6,7),(1,9),(3,10);
/*!40000 ALTER TABLE `enclosure_neighbors` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `enclosures`
--

DROP TABLE IF EXISTS `enclosures`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `enclosures` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `is_warm` char(1) NOT NULL,
  `length` decimal(6,2) NOT NULL,
  `width` decimal(6,2) NOT NULL,
  `height` decimal(6,2) NOT NULL,
  `notes` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`),
  CONSTRAINT `enclosures_chk_1` CHECK ((`is_warm` in (_utf8mb4'Y',_utf8mb4'N')))
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `enclosures`
--

LOCK TABLES `enclosures` WRITE;
/*!40000 ALTER TABLE `enclosures` DISABLE KEYS */;
INSERT INTO `enclosures` VALUES (1,'Вольер 1','Y',30.00,20.00,8.00,'Для тигров, требуется отопление'),(2,'Вольер 2','Y',35.00,25.00,9.00,'Для львов, требуется отопление'),(3,'Клетка 1','N',20.00,15.00,6.00,'Для волков, без отопления'),(4,'Вольер 3','Y',40.00,30.00,10.00,'Для зебр'),(5,'Клетка 2','Y',15.00,12.00,5.00,'Для обезьян'),(6,'Вольер 4','Y',25.00,20.00,8.00,'Для панд'),(7,'Вольер 5','Y',30.00,25.00,8.00,'Для кенгуру'),(8,'Клетка 3','N',20.00,18.00,6.00,'Для пингвинов, без отопления'),(9,'Вольер 6','Y',30.00,20.00,8.00,'Резерв для тигров'),(10,'Клетка 4','N',20.00,15.00,6.00,'Резерв для волков');
/*!40000 ALTER TABLE `enclosures` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `feed_inventory`
--

DROP TABLE IF EXISTS `feed_inventory`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `feed_inventory` (
  `id` int NOT NULL AUTO_INCREMENT,
  `feed_item_id` int DEFAULT NULL,
  `quantity` decimal(10,2) NOT NULL,
  `received_date` timestamp NOT NULL,
  PRIMARY KEY (`id`),
  KEY `feed_item_id` (`feed_item_id`),
  CONSTRAINT `feed_inventory_ibfk_1` FOREIGN KEY (`feed_item_id`) REFERENCES `feed_items` (`id`) ON DELETE CASCADE,
  CONSTRAINT `feed_inventory_chk_1` CHECK ((`quantity` >= 0))
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `feed_inventory`
--

LOCK TABLES `feed_inventory` WRITE;
/*!40000 ALTER TABLE `feed_inventory` DISABLE KEYS */;
INSERT INTO `feed_inventory` VALUES (1,1,150.00,'2024-01-10 09:00:00'),(2,6,80.00,'2024-01-12 10:30:00'),(3,4,50.00,'2024-01-15 11:00:00'),(4,5,60.00,'2024-01-18 12:00:00');
/*!40000 ALTER TABLE `feed_inventory` ENABLE KEYS */;
UNLOCK TABLES;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`%`*/ /*!50003 TRIGGER `trg_feed_inventory_received_date_insert` BEFORE INSERT ON `feed_inventory` FOR EACH ROW BEGIN
    CALL check_date_not_in_future(NEW.received_date, 'Дата поступления корма не может быть в будущем');
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`%`*/ /*!50003 TRIGGER `trg_feed_inventory_received_date_update` BEFORE UPDATE ON `feed_inventory` FOR EACH ROW BEGIN
    CALL check_date_not_in_future(NEW.received_date, 'Дата поступления корма не может быть в будущем');
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Table structure for table `feed_items`
--

DROP TABLE IF EXISTS `feed_items`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `feed_items` (
  `id` int NOT NULL AUTO_INCREMENT,
  `feed_type` int DEFAULT NULL,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`),
  KEY `feed_type` (`feed_type`),
  CONSTRAINT `feed_items_ibfk_1` FOREIGN KEY (`feed_type`) REFERENCES `feed_types` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `feed_items`
--

LOCK TABLES `feed_items` WRITE;
/*!40000 ALTER TABLE `feed_items` DISABLE KEYS */;
INSERT INTO `feed_items` VALUES (1,1,'Фрукты'),(2,1,'Овощи'),(3,2,'Мыши'),(4,2,'Птицы'),(5,2,'Корм для рыб'),(6,2,'Рыба'),(7,3,'Говядина'),(8,4,'Смешанный корм');
/*!40000 ALTER TABLE `feed_items` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `feed_orders`
--

DROP TABLE IF EXISTS `feed_orders`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `feed_orders` (
  `id` int NOT NULL AUTO_INCREMENT,
  `feed_supplier_id` int DEFAULT NULL,
  `feed_item_id` int DEFAULT NULL,
  `ordered_quantity` decimal(10,2) NOT NULL,
  `order_date` date NOT NULL,
  `delivery_date` date DEFAULT NULL,
  `price` decimal(10,2) NOT NULL,
  `status` varchar(50) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `feed_supplier_id` (`feed_supplier_id`),
  KEY `feed_item_id` (`feed_item_id`),
  CONSTRAINT `feed_orders_ibfk_1` FOREIGN KEY (`feed_supplier_id`) REFERENCES `feed_suppliers` (`id`) ON DELETE CASCADE,
  CONSTRAINT `feed_orders_ibfk_2` FOREIGN KEY (`feed_item_id`) REFERENCES `feed_items` (`id`) ON DELETE CASCADE,
  CONSTRAINT `feed_orders_chk_1` CHECK ((`status` in (_utf8mb4'Оформлен',_utf8mb4'Доставлен',_utf8mb4'В пути',_utf8mb4'Отменен'))),
  CONSTRAINT `feed_orders_chk_2` CHECK (((`delivery_date` is null) or (`delivery_date` >= `order_date`)))
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `feed_orders`
--

LOCK TABLES `feed_orders` WRITE;
/*!40000 ALTER TABLE `feed_orders` DISABLE KEYS */;
INSERT INTO `feed_orders` VALUES (1,1,6,50.00,'2024-02-20','2024-02-25',1500.00,'Доставлен'),(2,2,5,40.00,'2024-02-22','2024-02-28',900.00,'В пути'),(3,1,1,200.00,'2024-03-01','2024-03-05',5000.00,'Доставлен'),(4,2,4,100.00,'2024-03-05',NULL,2500.00,'В пути'),(5,1,2,25.00,'2024-04-02','2024-04-07',500.00,'Доставлен');
/*!40000 ALTER TABLE `feed_orders` ENABLE KEYS */;
UNLOCK TABLES;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`%`*/ /*!50003 TRIGGER `trg_feed_orders_order_date_insert` BEFORE INSERT ON `feed_orders` FOR EACH ROW BEGIN
    CALL check_date_not_in_future(NEW.order_date, 'Дата заказа не может быть в будущем');
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`%`*/ /*!50003 TRIGGER `trg_feed_orders_order_date_update` BEFORE UPDATE ON `feed_orders` FOR EACH ROW BEGIN
    CALL check_date_not_in_future(NEW.order_date, 'Дата заказа не может быть в будущем');
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`%`*/ /*!50003 TRIGGER `trg_feed_orders_delivered_inventory` AFTER UPDATE ON `feed_orders` FOR EACH ROW BEGIN
    IF NEW.status = 'Доставлен' AND OLD.status <> 'Доставлен' THEN
        IF EXISTS (SELECT 1 FROM feed_inventory WHERE feed_item_id = NEW.feed_item_id) THEN
            UPDATE feed_inventory
            SET quantity = quantity + NEW.ordered_quantity,
                received_date = NOW()
            WHERE feed_item_id = NEW.feed_item_id;
        ELSE
            INSERT INTO feed_inventory (feed_item_id, quantity, received_date)
            VALUES (NEW.feed_item_id, NEW.ordered_quantity, NOW());
        END IF;
    END IF;
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Table structure for table `feed_production`
--

DROP TABLE IF EXISTS `feed_production`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `feed_production` (
  `id` int NOT NULL AUTO_INCREMENT,
  `feed_item_id` int DEFAULT NULL,
  `production_date` date NOT NULL,
  `quantity` decimal(10,2) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `feed_item_id` (`feed_item_id`),
  CONSTRAINT `feed_production_ibfk_1` FOREIGN KEY (`feed_item_id`) REFERENCES `feed_items` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `feed_production`
--

LOCK TABLES `feed_production` WRITE;
/*!40000 ALTER TABLE `feed_production` DISABLE KEYS */;
INSERT INTO `feed_production` VALUES (1,2,'2024-02-01',30.00),(2,8,'2024-02-05',50.00),(3,3,'2024-02-07',5.00);
/*!40000 ALTER TABLE `feed_production` ENABLE KEYS */;
UNLOCK TABLES;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`%`*/ /*!50003 TRIGGER `trg_feed_production_production_date_insert` BEFORE INSERT ON `feed_production` FOR EACH ROW BEGIN
    CALL check_date_not_in_future(NEW.production_date, 'Дата производства не может быть в будущем');
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`%`*/ /*!50003 TRIGGER `trg_feed_production_inventory` AFTER INSERT ON `feed_production` FOR EACH ROW BEGIN
    DECLARE cur_received_date TIMESTAMP;
    IF EXISTS (SELECT 1 FROM feed_inventory WHERE feed_item_id = NEW.feed_item_id) THEN
        SELECT received_date INTO cur_received_date FROM feed_inventory WHERE feed_item_id = NEW.feed_item_id;
        IF NEW.production_date > cur_received_date THEN
            UPDATE feed_inventory
            SET quantity = quantity + NEW.quantity,
                received_date = NEW.production_date
            WHERE feed_item_id = NEW.feed_item_id;
        ELSE
            UPDATE feed_inventory
            SET quantity = quantity + NEW.quantity
            WHERE feed_item_id = NEW.feed_item_id;
        END IF;
    ELSE
        INSERT INTO feed_inventory (feed_item_id, quantity, received_date)
        VALUES (NEW.feed_item_id, NEW.quantity, NEW.production_date);
    END IF;
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`%`*/ /*!50003 TRIGGER `trg_feed_production_production_date_update` BEFORE UPDATE ON `feed_production` FOR EACH ROW BEGIN
    CALL check_date_not_in_future(NEW.production_date, 'Дата производства не может быть в будущем');
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Table structure for table `feed_suppliers`
--

DROP TABLE IF EXISTS `feed_suppliers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `feed_suppliers` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `phone` varchar(20) NOT NULL,
  `address` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `feed_suppliers`
--

LOCK TABLES `feed_suppliers` WRITE;
/*!40000 ALTER TABLE `feed_suppliers` DISABLE KEYS */;
INSERT INTO `feed_suppliers` VALUES (1,'Поставщик №1','+7-123-456-7890','г. Москва, ул. Ленина, д.1'),(2,'Поставщик №2','+7-987-654-3210','г. Санкт-Петербург, пр-т. Мира, д.10');
/*!40000 ALTER TABLE `feed_suppliers` ENABLE KEYS */;
UNLOCK TABLES;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`%`*/ /*!50003 TRIGGER `trg_feed_suppliers_phone_format_insert` BEFORE INSERT ON `feed_suppliers` FOR EACH ROW BEGIN
    DECLARE cleaned VARCHAR(20);
    SET cleaned = REGEXP_REPLACE(NEW.phone, '[^0-9]', '');
    IF LEFT(cleaned, 1) = '8' THEN
        SET cleaned = CONCAT('7', SUBSTRING(cleaned, 2));
    END IF;
    IF LEFT(cleaned, 1) = '7' AND LENGTH(cleaned) = 11 THEN
        SET NEW.phone = CONCAT('+7-', SUBSTRING(cleaned, 2, 3), '-', SUBSTRING(cleaned, 5, 3), '-', SUBSTRING(cleaned, 8, 4));
    ELSE
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Телефон поставщика должен быть российским номером (+7-XXX-XXX-XXXX)';
    END IF;
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`%`*/ /*!50003 TRIGGER `trg_feed_suppliers_phone_format_update` BEFORE UPDATE ON `feed_suppliers` FOR EACH ROW BEGIN
    DECLARE cleaned VARCHAR(20);
    SET cleaned = REGEXP_REPLACE(NEW.phone, '[^0-9]', '');
    IF LEFT(cleaned, 1) = '8' THEN
        SET cleaned = CONCAT('7', SUBSTRING(cleaned, 2));
    END IF;
    IF LEFT(cleaned, 1) = '7' AND LENGTH(cleaned) = 11 THEN
        SET NEW.phone = CONCAT('+7-', SUBSTRING(cleaned, 2, 3), '-', SUBSTRING(cleaned, 5, 3), '-', SUBSTRING(cleaned, 8, 4));
    ELSE
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Телефон поставщика должен быть российским номером (+7-XXX-XXX-XXXX)';
    END IF;
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Table structure for table `feed_types`
--

DROP TABLE IF EXISTS `feed_types`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `feed_types` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `feed_types`
--

LOCK TABLES `feed_types` WRITE;
/*!40000 ALTER TABLE `feed_types` DISABLE KEYS */;
INSERT INTO `feed_types` VALUES (2,'Живой'),(4,'Комбикорм'),(3,'Мясо'),(1,'Растительный');
/*!40000 ALTER TABLE `feed_types` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `feeding_classifications`
--

DROP TABLE IF EXISTS `feeding_classifications`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `feeding_classifications` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`),
  CONSTRAINT `feeding_classifications_chk_1` CHECK ((`name` in (_utf8mb4'Травоядное',_utf8mb4'Хищник')))
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `feeding_classifications`
--

LOCK TABLES `feeding_classifications` WRITE;
/*!40000 ALTER TABLE `feeding_classifications` DISABLE KEYS */;
INSERT INTO `feeding_classifications` VALUES (2,'Травоядное'),(1,'Хищник');
/*!40000 ALTER TABLE `feeding_classifications` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `incompatible_species`
--

DROP TABLE IF EXISTS `incompatible_species`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `incompatible_species` (
  `species1_id` int NOT NULL,
  `species2_id` int NOT NULL,
  PRIMARY KEY (`species1_id`,`species2_id`),
  KEY `species2_id` (`species2_id`),
  CONSTRAINT `incompatible_species_ibfk_1` FOREIGN KEY (`species1_id`) REFERENCES `species` (`id`) ON DELETE CASCADE,
  CONSTRAINT `incompatible_species_ibfk_2` FOREIGN KEY (`species2_id`) REFERENCES `species` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `incompatible_species`
--

LOCK TABLES `incompatible_species` WRITE;
/*!40000 ALTER TABLE `incompatible_species` DISABLE KEYS */;
INSERT INTO `incompatible_species` VALUES (1,2),(4,2),(5,2),(1,3),(4,3),(5,3),(1,6),(4,6),(5,6),(1,7),(4,7),(5,7),(1,8),(4,8),(5,8);
/*!40000 ALTER TABLE `incompatible_species` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `species`
--

DROP TABLE IF EXISTS `species`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `species` (
  `id` int NOT NULL AUTO_INCREMENT,
  `type_name` varchar(255) NOT NULL,
  `classification` int DEFAULT NULL,
  `climate_zone` int DEFAULT NULL,
  `need_warm` char(1) NOT NULL,
  `puberty_age` int NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `type_name` (`type_name`),
  KEY `classification` (`classification`),
  KEY `climate_zone` (`climate_zone`),
  CONSTRAINT `species_ibfk_1` FOREIGN KEY (`classification`) REFERENCES `feeding_classifications` (`id`) ON DELETE CASCADE,
  CONSTRAINT `species_ibfk_2` FOREIGN KEY (`climate_zone`) REFERENCES `climate_zones` (`id`) ON DELETE CASCADE,
  CONSTRAINT `species_chk_1` CHECK ((`need_warm` in (_utf8mb4'Y',_utf8mb4'N')))
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `species`
--

LOCK TABLES `species` WRITE;
/*!40000 ALTER TABLE `species` DISABLE KEYS */;
INSERT INTO `species` VALUES (1,'Тигр',1,2,'Y',3),(2,'Зебра',2,3,'Y',2),(3,'Обезьяна',2,1,'Y',4),(4,'Волк',1,4,'N',2),(5,'Лев',1,3,'Y',3),(6,'Панда',2,3,'Y',5),(7,'Кенгуру',2,2,'Y',2),(8,'Пингвин',2,5,'N',3);
/*!40000 ALTER TABLE `species` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `staff`
--

DROP TABLE IF EXISTS `staff`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `staff` (
  `id` int NOT NULL AUTO_INCREMENT,
  `last_name` varchar(255) NOT NULL,
  `first_name` varchar(255) NOT NULL,
  `middle_name` varchar(255) DEFAULT NULL,
  `gender` char(1) DEFAULT NULL,
  `birth_date` date NOT NULL,
  `hire_date` date NOT NULL,
  `salary` decimal(10,2) NOT NULL,
  `category_id` int DEFAULT NULL,
  `is_active` tinyint(1) NOT NULL DEFAULT '1',
  `username` varchar(255) NOT NULL,
  `password_hash` varchar(255) NOT NULL,
  `avatar_url` varchar(255) DEFAULT NULL,
  `avatar_original_url` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`),
  KEY `category_id` (`category_id`),
  CONSTRAINT `staff_ibfk_1` FOREIGN KEY (`category_id`) REFERENCES `staff_categories` (`id`) ON DELETE CASCADE,
  CONSTRAINT `staff_chk_1` CHECK ((`gender` in (_utf8mb4'М',_utf8mb4'Ж'))),
  CONSTRAINT `staff_chk_2` CHECK ((`hire_date` >= `birth_date`))
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `staff`
--

LOCK TABLES `staff` WRITE;
/*!40000 ALTER TABLE `staff` DISABLE KEYS */;
INSERT INTO `staff` VALUES (1,'Иванов','Алексей','Петрович','М','1980-03-15','2010-06-01',55000.00,1,1,'aivanov','scrypt:32768:8:1$0oorYplSAcdZ2CUM$21c06d4bde4045103a964f18c2c9051161fcb82715c77994a49b83451e9fae292194c03745ab69eabdead1aa74cdbfcd245d4600818b84c8f444ef4436421c9e',NULL,NULL),(2,'Сидорова','Мария','Ивановна','Ж','1985-07-22','2012-04-15',45000.00,2,1,'msidorova','scrypt:32768:8:1$4wcHuBXCKdEtxL7a$7a0b1f75a171e6f3da143b610752406d61298aa83703ef7c4ff9226dedc52a57ec2b7ed0dda39935cb5bdd873a964f118c537d75f04afbe61a09430561576359',NULL,NULL),(3,'Петров','Николай','Сергеевич','М','1978-11-05','2008-09-10',50000.00,3,1,'npetrov','scrypt:32768:8:1$HYtMpCwZAwYrtsh4$905174121e77c0f339e3f2df367123bddec18ddd65e9846c052fc24b66342a8408a70a580e7484197a51bcecf57db59954430d1728dc3d9b5bf51254cc2eacea',NULL,NULL),(4,'Кузнецова','Елена','Александровна','Ж','1990-01-30','2015-03-20',60000.00,4,1,'ekuznetsova','scrypt:32768:8:1$RkWaQ2LJaCdVZmpw$8bf58183b21605c38b0874db5195b0afb9dc0019c1fd59dfaae51cfb14f92403a9d3132dadcf8f6b8e5f845839f7780777283e6bfdf62a92d07a742a5f34bb5c',NULL,NULL),(5,'Морозов','Сергей','Васильевич','М','1982-05-12','2009-08-25',70000.00,5,1,'smorozov','scrypt:32768:8:1$QJJ2cbnjyhdszdeq$8a35d27f545e059ae890ca2851c3a6fff3d07057fc78a8862a213f679346d59471e2c573712672588ee6f7081a6a6d056b77a02c3826e46461eeb0db99bce219',NULL,NULL),(6,'Соколов','Дмитрий','Андреевич','М','1992-09-14','2020-02-18',58000.00,1,1,'dsokolov','scrypt:32768:8:1$r990J3yVKn8EEffF$c179a2e7ffc93890e500893e1f8b916aa2d634258202dbcf176925e5e792fe10aff95cacdf1a0ffd6ad6894d0a9f6658c0acce4ab194150c756b2ec43be07ba1',NULL,NULL),(7,'Козлова','Ольга','Викторовна','Ж','1995-04-22','2021-05-12',42000.00,2,1,'okozlova','scrypt:32768:8:1$Bf2x3WMZNwmq45OZ$88390cd6d85ed665ffeabc18c876a0385cbf550d1aa548969d7071ab8b0fec75ccae8e5380dc8a1631f86163a7b87aca58f266d07cbc338a4426eff6dc58ef23','/media/avatars/user_7_20250601_174324_avatar.jpg','/media/avatars/user_7_20250601_174324_original.jpg'),(8,'Орлов','Игорь','Николаевич','М','1990-12-03','2019-11-09',52000.00,3,1,'iorlov','scrypt:32768:8:1$QXzR5qcRi6q9balh$baefd80a8213ffda5fb463cf86a3dd7b714f018b029a5058070ded7ec03db079022e9b5cc1af15a01ecde998dba502e02cf546ad491556bfecf558956d0e69f7',NULL,NULL),(9,'Тихонов','Александр','Сергеевич','М','1987-06-28','2022-03-01',62000.00,4,1,'atikhonov','scrypt:32768:8:1$m0i4D2PadAIO30gp$860635b94cde767e7c1185adfefb39d4a3977e52e5ddb593c58edd389d08235a415d8d4450650434dc6f0e42159fda7655b15cef021845ecae8aa9405ade5dc4',NULL,NULL),(10,'Захарова','Екатерина','Дмитриевна','Ж','1989-10-11','2017-07-14',72000.00,5,1,'ezaharova','scrypt:32768:8:1$uQqAZsNpgZihaNLf$f26fb89a856e0b373a3b68a8c5467d5164b3fc071ef264cd102e9d442a3c7de07e793d780f98a183e1d98717747d566021f7f9873e012b9cda37e6824bf5cdbf','/media/avatars/user_10_20250601_154722_avatar.jpg','/media/avatars/user_10_20250601_154722_original.jpg');
/*!40000 ALTER TABLE `staff` ENABLE KEYS */;
UNLOCK TABLES;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`%`*/ /*!50003 TRIGGER `trg_staff_birth_date_insert` BEFORE INSERT ON `staff` FOR EACH ROW BEGIN
    CALL check_date_not_in_future(NEW.birth_date, 'Дата рождения не может быть в будущем');
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`%`*/ /*!50003 TRIGGER `trg_staff_hire_date_insert` BEFORE INSERT ON `staff` FOR EACH ROW BEGIN
    CALL check_date_not_in_future(NEW.hire_date, 'Дата найма не может быть в будущем');
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`%`*/ /*!50003 TRIGGER `trg_staff_birth_date_update` BEFORE UPDATE ON `staff` FOR EACH ROW BEGIN
    CALL check_date_not_in_future(NEW.birth_date, 'Дата рождения не может быть в будущем');
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`%`*/ /*!50003 TRIGGER `trg_staff_hire_date_update` BEFORE UPDATE ON `staff` FOR EACH ROW BEGIN
    CALL check_date_not_in_future(NEW.hire_date, 'Дата найма не может быть в будущем');
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Table structure for table `staff_attribute_values`
--

DROP TABLE IF EXISTS `staff_attribute_values`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `staff_attribute_values` (
  `staff_id` int NOT NULL,
  `attribute_id` int NOT NULL,
  `attribute_value` varchar(255) NOT NULL,
  PRIMARY KEY (`staff_id`,`attribute_id`),
  KEY `attribute_id` (`attribute_id`),
  CONSTRAINT `staff_attribute_values_ibfk_1` FOREIGN KEY (`staff_id`) REFERENCES `staff` (`id`) ON DELETE CASCADE,
  CONSTRAINT `staff_attribute_values_ibfk_2` FOREIGN KEY (`attribute_id`) REFERENCES `category_attributes` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `staff_attribute_values`
--

LOCK TABLES `staff_attribute_values` WRITE;
/*!40000 ALTER TABLE `staff_attribute_values` DISABLE KEYS */;
INSERT INTO `staff_attribute_values` VALUES (1,1,'Хирургия, терапия'),(1,2,'12'),(2,3,'Ночная'),(2,4,'Высокий'),(3,5,'Приматы'),(3,6,'Позитивное подкрепление'),(4,7,'Электрик'),(4,8,'Свидетельство №123'),(5,9,'Директор'),(5,10,'Администрация'),(6,1,'Общая практика'),(6,2,'4'),(7,3,'Дневная'),(7,4,'Средний'),(8,5,'Хищные птицы'),(8,6,'Кликер-тренинг'),(9,7,'Сантехник'),(9,8,'Свидетельство №456'),(10,9,'Заместитель директора'),(10,10,'Логистика');
/*!40000 ALTER TABLE `staff_attribute_values` ENABLE KEYS */;
UNLOCK TABLES;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`%`*/ /*!50003 TRIGGER `trg_staff_attribute_values_category_insert` BEFORE INSERT ON `staff_attribute_values` FOR EACH ROW BEGIN
    DECLARE cat_id INT;
    DECLARE cnt INT;
    SELECT category_id INTO cat_id FROM staff WHERE id = NEW.staff_id;
    SELECT COUNT(*) INTO cnt FROM category_attributes WHERE id = NEW.attribute_id AND category_id = cat_id;
    IF cnt = 0 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Атрибут не определён для категории сотрудника';
    END IF;
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`%`*/ /*!50003 TRIGGER `trg_staff_attribute_values_category_update` BEFORE UPDATE ON `staff_attribute_values` FOR EACH ROW BEGIN
    DECLARE cat_id INT;
    DECLARE cnt INT;
    SELECT category_id INTO cat_id FROM staff WHERE id = NEW.staff_id;
    SELECT COUNT(*) INTO cnt FROM category_attributes WHERE id = NEW.attribute_id AND category_id = cat_id;
    IF cnt = 0 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Атрибут не определён для категории сотрудника';
    END IF;
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Table structure for table `staff_categories`
--

DROP TABLE IF EXISTS `staff_categories`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `staff_categories` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `enclosure_access` char(1) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`),
  CONSTRAINT `staff_categories_chk_1` CHECK ((`enclosure_access` in (_utf8mb4'Y',_utf8mb4'N')))
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `staff_categories`
--

LOCK TABLES `staff_categories` WRITE;
/*!40000 ALTER TABLE `staff_categories` DISABLE KEYS */;
INSERT INTO `staff_categories` VALUES (1,'Ветеринар','Y'),(2,'Уборщик','Y'),(3,'Дрессировщик','Y'),(4,'Строитель-ремонтник','N'),(5,'Работник администрации','N');
/*!40000 ALTER TABLE `staff_categories` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `supplier_feed_types`
--

DROP TABLE IF EXISTS `supplier_feed_types`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `supplier_feed_types` (
  `supplier_id` int NOT NULL,
  `feed_type_id` int NOT NULL,
  PRIMARY KEY (`supplier_id`,`feed_type_id`),
  KEY `feed_type_id` (`feed_type_id`),
  CONSTRAINT `supplier_feed_types_ibfk_1` FOREIGN KEY (`supplier_id`) REFERENCES `feed_suppliers` (`id`) ON DELETE CASCADE,
  CONSTRAINT `supplier_feed_types_ibfk_2` FOREIGN KEY (`feed_type_id`) REFERENCES `feed_types` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `supplier_feed_types`
--

LOCK TABLES `supplier_feed_types` WRITE;
/*!40000 ALTER TABLE `supplier_feed_types` DISABLE KEYS */;
INSERT INTO `supplier_feed_types` VALUES (1,1),(2,2),(1,3),(2,4);
/*!40000 ALTER TABLE `supplier_feed_types` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `vaccines`
--

DROP TABLE IF EXISTS `vaccines`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `vaccines` (
  `id` int NOT NULL AUTO_INCREMENT,
  `disease_id` int DEFAULT NULL,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`),
  KEY `disease_id` (`disease_id`),
  CONSTRAINT `vaccines_ibfk_1` FOREIGN KEY (`disease_id`) REFERENCES `diseases` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `vaccines`
--

LOCK TABLES `vaccines` WRITE;
/*!40000 ALTER TABLE `vaccines` DISABLE KEYS */;
INSERT INTO `vaccines` VALUES (1,1,'Антипаразитарная вакцина'),(2,2,'Противореспираторная вакцина');
/*!40000 ALTER TABLE `vaccines` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `zoo_exchanges`
--

DROP TABLE IF EXISTS `zoo_exchanges`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `zoo_exchanges` (
  `id` int NOT NULL AUTO_INCREMENT,
  `animal_id` int DEFAULT NULL,
  `exchange_date` date NOT NULL,
  `exchange_type` varchar(50) DEFAULT NULL,
  `partner_zoo` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `animal_id` (`animal_id`),
  CONSTRAINT `zoo_exchanges_ibfk_1` FOREIGN KEY (`animal_id`) REFERENCES `animals` (`id`) ON DELETE CASCADE,
  CONSTRAINT `zoo_exchanges_chk_1` CHECK ((`exchange_type` in (_utf8mb4'Входящий',_utf8mb4'Исходящий')))
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `zoo_exchanges`
--

LOCK TABLES `zoo_exchanges` WRITE;
/*!40000 ALTER TABLE `zoo_exchanges` DISABLE KEYS */;
INSERT INTO `zoo_exchanges` VALUES (1,20,'2023-09-01','Исходящий','Зоопарк Северный'),(2,21,'2023-09-05','Исходящий','Зоопарк Южный'),(3,22,'2023-09-05','Исходящий','Зоопарк Южный'),(4,15,'2023-09-05','Входящий','Зоопарк Южный');
/*!40000 ALTER TABLE `zoo_exchanges` ENABLE KEYS */;
UNLOCK TABLES;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`%`*/ /*!50003 TRIGGER `trg_zoo_exchanges_exchange_date_insert` BEFORE INSERT ON `zoo_exchanges` FOR EACH ROW BEGIN
    CALL check_date_not_in_future(NEW.exchange_date, 'Дата обмена не может быть в будущем');
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`%`*/ /*!50003 TRIGGER `trg_zoo_exchanges_exchange_date_update` BEFORE UPDATE ON `zoo_exchanges` FOR EACH ROW BEGIN
    CALL check_date_not_in_future(NEW.exchange_date, 'Дата обмена не может быть в будущем');
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Dumping events for database 'zoo_db'
--

--
-- Dumping routines for database 'zoo_db'
--
/*!50003 DROP FUNCTION IF EXISTS `get_years_diff` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`%` FUNCTION `get_years_diff`(input_date DATE) RETURNS int
    DETERMINISTIC
BEGIN
    RETURN TIMESTAMPDIFF(YEAR, input_date, CURDATE());
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `check_animals_compatibility` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`%` PROCEDURE `check_animals_compatibility`(
    IN new_species_id INT,
    IN new_enclosure_id INT
)
BEGIN
    DECLARE cnt INT;
    SELECT COUNT(*) INTO cnt
    FROM animals a
    JOIN enclosures e ON a.enclosure_id = e.id
    JOIN enclosure_neighbors n 
      ON ( (n.enclosure1_id = new_enclosure_id AND n.enclosure2_id = e.id)
        OR (n.enclosure2_id = new_enclosure_id AND n.enclosure1_id = e.id) )
    JOIN incompatible_species i 
      ON ( (i.species1_id = new_species_id AND i.species2_id = a.species_id)
        OR (i.species2_id = new_species_id AND i.species1_id = a.species_id) )
    WHERE a.enclosure_id IS NOT NULL;
    IF cnt > 0 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'В соседних клетках есть несовместимые виды';
    END IF;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `check_date_not_in_future` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`%` PROCEDURE `check_date_not_in_future`(
    IN some_date DATE,
    IN err_message VARCHAR(255)
)
BEGIN
    IF some_date > CURDATE() THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = err_message;
    END IF;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `get_all_offspring_count` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`%` PROCEDURE `get_all_offspring_count`(IN animal_id INT, OUT offspring_count INT)
BEGIN
	DECLARE new_rows INT DEFAULT 1;
	DECLARE id_list TEXT;
	DECLARE offspring_list TEXT DEFAULT '';
	SET offspring_count = 0;
    DROP TEMPORARY TABLE IF EXISTS tmp_offspring;
    DROP TEMPORARY TABLE IF EXISTS tmp_new;
    CREATE TEMPORARY TABLE tmp_offspring (id INT PRIMARY KEY);
    CREATE TEMPORARY TABLE tmp_new (id INT PRIMARY KEY);

    -- Добавляем прямых детей
    INSERT INTO tmp_offspring (id)
    SELECT id FROM animals WHERE parent1_id = animal_id OR parent2_id = animal_id;

    -- Формируем начальный offspring_list
    SELECT GROUP_CONCAT(id) INTO offspring_list FROM tmp_offspring;

    WHILE new_rows > 0 DO
        TRUNCATE TABLE tmp_new;
        INSERT IGNORE INTO tmp_new (id) SELECT id FROM tmp_offspring;

        -- Получаем список id через запятую
        SELECT GROUP_CONCAT(id) INTO id_list FROM tmp_new;

        SET new_rows = 0;

        IF id_list IS NOT NULL THEN
            -- Потомки по parent1_id
            INSERT IGNORE INTO tmp_offspring (id)
            SELECT a.id
            FROM animals a
            WHERE FIND_IN_SET(a.parent1_id, id_list)
              AND (offspring_list IS NULL OR FIND_IN_SET(a.id, offspring_list) = 0);
            SET new_rows = new_rows + ROW_COUNT();

            -- Потомки по parent2_id
            INSERT IGNORE INTO tmp_offspring (id)
            SELECT a.id
            FROM animals a
            WHERE FIND_IN_SET(a.parent2_id, id_list)
              AND (offspring_list IS NULL OR FIND_IN_SET(a.id, offspring_list) = 0);
            SET new_rows = new_rows + ROW_COUNT();

            -- Обновляем offspring_list
            SELECT GROUP_CONCAT(id) INTO offspring_list FROM tmp_offspring;
        END IF;
    END WHILE;

    SELECT COUNT(*) INTO offspring_count FROM tmp_offspring;
    DROP TEMPORARY TABLE tmp_offspring;
    DROP TEMPORARY TABLE tmp_new;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-06-02 15:27:31
