CREATE DATABASE  IF NOT EXISTS `movingcampus` /*!40100 DEFAULT CHARACTER SET utf8 */;
USE `movingcampus`;
-- MySQL dump 10.13  Distrib 5.5.32, for Linux (x86_64)
--
-- Host: 127.0.0.1    Database: movingcampus
-- ------------------------------------------------------
-- Server version	5.5.32-log

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `allinonecard`
--

DROP TABLE IF EXISTS `allinonecard`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `allinonecard` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `left` float DEFAULT NULL,
  `status` tinyint(1) DEFAULT NULL,
  `todaypay` float DEFAULT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `fk_allinonecard_1` FOREIGN KEY (`id`) REFERENCES `student` (`idstudent`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `allinonecard`
--

LOCK TABLES `allinonecard` WRITE;
/*!40000 ALTER TABLE `allinonecard` DISABLE KEYS */;
INSERT INTO `allinonecard` VALUES (1,1000.05,1,10.11),(2,1000,0,10.22),(3,1000,0,11.25);
/*!40000 ALTER TABLE `allinonecard` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `building`
--

DROP TABLE IF EXISTS `building`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `building` (
  `idbuilding` int(11) NOT NULL,
  `name` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`idbuilding`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `building`
--

LOCK TABLES `building` WRITE;
/*!40000 ALTER TABLE `building` DISABLE KEYS */;
INSERT INTO `building` VALUES (1,'A'),(2,'B'),(3,'C'),(4,'D'),(5,'E'),(6,'理科群A'),(7,'文科群6');
/*!40000 ALTER TABLE `building` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `catagory`
--

DROP TABLE IF EXISTS `catagory`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `catagory` (
  `idcatagory` int(11) NOT NULL,
  `name` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`idcatagory`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `catagory`
--

LOCK TABLES `catagory` WRITE;
/*!40000 ALTER TABLE `catagory` DISABLE KEYS */;
INSERT INTO `catagory` VALUES (1,'放假通知'),(2,'学院活动'),(3,'获奖通知'),(4,'失物招领');
/*!40000 ALTER TABLE `catagory` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `classchoosecourse`
--

DROP TABLE IF EXISTS `classchoosecourse`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `classchoosecourse` (
  `idclass` int(11) NOT NULL AUTO_INCREMENT,
  `classid` int(11) DEFAULT NULL,
  `course` int(11) DEFAULT NULL,
  `keshi` int(11) DEFAULT NULL,
  `zhoushu` int(11) DEFAULT NULL,
  PRIMARY KEY (`idclass`),
  KEY `fk_classcourse_1_idx` (`classid`),
  KEY `fk_classcourse_2_idx` (`course`),
  CONSTRAINT `fk_classcourse_1` FOREIGN KEY (`classid`) REFERENCES `classes` (`classid`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_classcourse_2` FOREIGN KEY (`course`) REFERENCES `course` (`idcourse`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `classchoosecourse`
--

LOCK TABLES `classchoosecourse` WRITE;
/*!40000 ALTER TABLE `classchoosecourse` DISABLE KEYS */;
/*!40000 ALTER TABLE `classchoosecourse` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `classes`
--

DROP TABLE IF EXISTS `classes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `classes` (
  `classid` int(11) NOT NULL,
  `number` int(11) DEFAULT NULL,
  `institudte` int(11) DEFAULT NULL,
  `numstu` int(11) DEFAULT NULL,
  PRIMARY KEY (`classid`),
  KEY `fk_classes_1_idx` (`institudte`),
  CONSTRAINT `fk_classes_1` FOREIGN KEY (`institudte`) REFERENCES `institute` (`idinstitute`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `classes`
--

LOCK TABLES `classes` WRITE;
/*!40000 ALTER TABLE `classes` DISABLE KEYS */;
INSERT INTO `classes` VALUES (1,7,1,46),(2,8,1,40),(3,1,2,50);
/*!40000 ALTER TABLE `classes` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `classincourse`
--

DROP TABLE IF EXISTS `classincourse`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `classincourse` (
  `classid` int(11) DEFAULT NULL,
  `timetable` int(11) NOT NULL,
  `course` int(11) DEFAULT NULL,
  `room` int(11) DEFAULT NULL,
  `teacher` int(11) DEFAULT NULL,
  PRIMARY KEY (`timetable`),
  KEY `fk_classincourse_1_idx` (`classid`),
  KEY `fk_classincourse_2_idx` (`course`),
  KEY `fk_classincourse_3_idx` (`teacher`),
  KEY `fk_classincourse_4_idx` (`timetable`),
  KEY `fk_classincourse_5_idx` (`room`),
  CONSTRAINT `fk_classincourse_1` FOREIGN KEY (`classid`) REFERENCES `classes` (`classid`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_classincourse_2` FOREIGN KEY (`course`) REFERENCES `course` (`idcourse`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_classincourse_3` FOREIGN KEY (`teacher`) REFERENCES `teacher` (`idteacher`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_classincourse_4` FOREIGN KEY (`timetable`) REFERENCES `timetable` (`idtimetable`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_classincourse_5` FOREIGN KEY (`room`) REFERENCES `room` (`idroom`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `classincourse`
--

LOCK TABLES `classincourse` WRITE;
/*!40000 ALTER TABLE `classincourse` DISABLE KEYS */;
/*!40000 ALTER TABLE `classincourse` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `course`
--

DROP TABLE IF EXISTS `course`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `course` (
  `idcourse` int(11) NOT NULL,
  `selectionCount` varchar(45) DEFAULT NULL,
  `selectionLimit` varchar(45) DEFAULT NULL,
  `name` varchar(45) DEFAULT NULL,
  `credit` smallint(6) DEFAULT NULL,
  `summary` varchar(45) DEFAULT NULL,
  `institudte` int(11) DEFAULT NULL,
  PRIMARY KEY (`idcourse`),
  CONSTRAINT `fk_course_1` FOREIGN KEY (`idcourse`) REFERENCES `institute` (`idinstitute`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `course`
--

LOCK TABLES `course` WRITE;
/*!40000 ALTER TABLE `course` DISABLE KEYS */;
INSERT INTO `course` VALUES (1,'50','50','C++',4,'C++',NULL);
/*!40000 ALTER TABLE `course` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `institute`
--

DROP TABLE IF EXISTS `institute`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `institute` (
  `idinstitute` int(11) NOT NULL,
  `name` varchar(45) DEFAULT NULL,
  `building` int(11) DEFAULT NULL,
  PRIMARY KEY (`idinstitute`),
  KEY `fk_institute_1_idx` (`building`),
  CONSTRAINT `fk_institute_1` FOREIGN KEY (`building`) REFERENCES `building` (`idbuilding`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `institute`
--

LOCK TABLES `institute` WRITE;
/*!40000 ALTER TABLE `institute` DISABLE KEYS */;
INSERT INTO `institute` VALUES (1,'软件学院',6),(2,'美术与设计学院',7);
/*!40000 ALTER TABLE `institute` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `news`
--

DROP TABLE IF EXISTS `news`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `news` (
  `idnews` int(11) NOT NULL,
  `title` varchar(45) DEFAULT NULL,
  `content` varchar(45) DEFAULT NULL,
  `time` datetime DEFAULT NULL,
  `catagory` int(11) DEFAULT NULL,
  PRIMARY KEY (`idnews`),
  KEY `fk_news_1_idx` (`catagory`),
  CONSTRAINT `fk_news_1` FOREIGN KEY (`catagory`) REFERENCES `catagory` (`idcatagory`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `news`
--

LOCK TABLES `news` WRITE;
/*!40000 ALTER TABLE `news` DISABLE KEYS */;
INSERT INTO `news` VALUES (1,'2013寒假放假通知','测试测试吊丝没有春天','2012-12-04 10:00:00',1),(2,'2013暑假放假通知','吊丝没有春天','2013-06-15 10:00:00',1),(3,'2014寒假放假通知','吊丝依旧没有春天','2013-12-06 10:00:00',1),(4,'失物招领','何杰丢失充气娃娃','2013-12-07 08:00:00',4);
/*!40000 ALTER TABLE `news` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `room`
--

DROP TABLE IF EXISTS `room`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `room` (
  `idroom` int(11) NOT NULL,
  `roomnum` int(11) DEFAULT NULL,
  `building` int(11) DEFAULT NULL,
  `manager` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`idroom`),
  KEY `fk_room_1_idx` (`building`),
  CONSTRAINT `fk_room_1` FOREIGN KEY (`building`) REFERENCES `building` (`idbuilding`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='教室\n';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `room`
--

LOCK TABLES `room` WRITE;
/*!40000 ALTER TABLE `room` DISABLE KEYS */;
/*!40000 ALTER TABLE `room` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `roomstatus`
--

DROP TABLE IF EXISTS `roomstatus`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `roomstatus` (
  `idtroom` int(11) NOT NULL,
  `timetable` int(11) DEFAULT NULL,
  `status` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`idtroom`),
  KEY `fk_roomstatus_1_idx` (`idtroom`),
  CONSTRAINT `fk_roomstatus_1` FOREIGN KEY (`idtroom`) REFERENCES `room` (`idroom`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `roomstatus`
--

LOCK TABLES `roomstatus` WRITE;
/*!40000 ALTER TABLE `roomstatus` DISABLE KEYS */;
/*!40000 ALTER TABLE `roomstatus` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `selection`
--

DROP TABLE IF EXISTS `selection`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `selection` (
  `idselect` int(11) NOT NULL,
  `student` int(11) DEFAULT NULL,
  `course` int(11) DEFAULT NULL,
  `result` int(11) DEFAULT NULL,
  PRIMARY KEY (`idselect`),
  KEY `fk_choose_1_idx` (`course`),
  KEY `fk_selection_1_idx` (`student`),
  CONSTRAINT `fk_choose_1` FOREIGN KEY (`course`) REFERENCES `course` (`idcourse`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_selection_1` FOREIGN KEY (`student`) REFERENCES `student` (`idstudent`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `selection`
--

LOCK TABLES `selection` WRITE;
/*!40000 ALTER TABLE `selection` DISABLE KEYS */;
/*!40000 ALTER TABLE `selection` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `student`
--

DROP TABLE IF EXISTS `student`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `student` (
  `idstudent` int(11) NOT NULL,
  `classid` int(11) DEFAULT NULL,
  `cardid` varchar(15) DEFAULT NULL,
  `name` varchar(15) DEFAULT NULL,
  `sex` tinyint(1) DEFAULT NULL,
  `phonenum` varchar(45) DEFAULT NULL,
  `role` tinyint(1) DEFAULT NULL,
  `pid` varchar(30) DEFAULT NULL,
  `lastpid` varchar(6) DEFAULT NULL,
  `institute` int(11) DEFAULT NULL,
  PRIMARY KEY (`idstudent`),
  KEY `fk_student_1_idx` (`classid`),
  KEY `fk_student_2_idx` (`institute`),
  CONSTRAINT `fk_student_1` FOREIGN KEY (`classid`) REFERENCES `classes` (`classid`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_student_2` FOREIGN KEY (`institute`) REFERENCES `institute` (`idinstitute`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `student`
--

LOCK TABLES `student` WRITE;
/*!40000 ALTER TABLE `student` DISABLE KEYS */;
INSERT INTO `student` VALUES (1,1,'2011011614','刘昭良',1,'18203233833',1,'371327199201152513','152513',1),(2,2,'2011011661','何杰',0,'18203233001',1,'421302199110072996','072996',1),(3,3,'2011011111','测试',0,'18203233000',0,'111111111111111111','111111',2);
/*!40000 ALTER TABLE `student` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `teacher`
--

DROP TABLE IF EXISTS `teacher`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `teacher` (
  `idteacher` int(11) NOT NULL,
  `name` varchar(45) DEFAULT NULL,
  `sex` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`idteacher`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `teacher`
--

LOCK TABLES `teacher` WRITE;
/*!40000 ALTER TABLE `teacher` DISABLE KEYS */;
/*!40000 ALTER TABLE `teacher` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `teachercourse`
--

DROP TABLE IF EXISTS `teachercourse`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `teachercourse` (
  `idteachers` int(11) NOT NULL,
  `teacher` int(11) DEFAULT NULL,
  `course` int(11) DEFAULT NULL,
  PRIMARY KEY (`idteachers`),
  KEY `fk_teachercourse_1_idx` (`course`),
  KEY `fk_teachercourse_2_idx` (`teacher`),
  CONSTRAINT `fk_teachercourse_1` FOREIGN KEY (`course`) REFERENCES `course` (`idcourse`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_teachercourse_2` FOREIGN KEY (`teacher`) REFERENCES `teacher` (`idteacher`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `teachercourse`
--

LOCK TABLES `teachercourse` WRITE;
/*!40000 ALTER TABLE `teachercourse` DISABLE KEYS */;
/*!40000 ALTER TABLE `teachercourse` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `timetable`
--

DROP TABLE IF EXISTS `timetable`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `timetable` (
  `idtimetable` int(11) NOT NULL,
  `week` varchar(45) DEFAULT NULL,
  `jieci` varchar(45) DEFAULT NULL,
  `zhouci` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`idtimetable`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `timetable`
--

LOCK TABLES `timetable` WRITE;
/*!40000 ALTER TABLE `timetable` DISABLE KEYS */;
/*!40000 ALTER TABLE `timetable` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2013-12-07 22:14:07
