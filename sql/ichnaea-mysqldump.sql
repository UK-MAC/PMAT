-- MySQL dump 10.14  Distrib 5.5.29-MariaDB, for Linux (x86_64)
--
-- Host: localhost    Database: ichnaea
-- ------------------------------------------------------
-- Server version	5.1.54-rel12.5

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
-- Table structure for table `Analysis`
--

DROP TABLE IF EXISTS `Analysis`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Analysis` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `Creator` varchar(50) NOT NULL,
  `Notes` varchar(200) DEFAULT NULL,
  `Private` tinyint(1) NOT NULL DEFAULT '1',
  `Date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `AnalysisAxis`
--

DROP TABLE IF EXISTS `AnalysisAxis`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `AnalysisAxis` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `AnalysisID` int(11) NOT NULL,
  `xAxis` int(11) NOT NULL,
  `yAxis` int(11) NOT NULL,
  `yAxisType` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `AnalysisAxisLabel`
--

DROP TABLE IF EXISTS `AnalysisAxisLabel`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `AnalysisAxisLabel` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `AnalysisID` int(11) NOT NULL,
  `OldText` varchar(200) NOT NULL,
  `NewText` varchar(200) NOT NULL,
  PRIMARY KEY (`ID`),
  KEY `AnalysisID` (`AnalysisID`),
  CONSTRAINT `AnalysisAxisLabel_ibfk_1` FOREIGN KEY (`AnalysisID`) REFERENCES `Analysis` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `AnalysisCriteria`
--

DROP TABLE IF EXISTS `AnalysisCriteria`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `AnalysisCriteria` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `AnalysisID` int(11) NOT NULL,
  `Sequence` int(11) NOT NULL,
  `Category` varchar(20) NOT NULL,
  `Field` varchar(50) NOT NULL,
  `Rank` int(11) DEFAULT NULL,
  `Comparator` varchar(20) NOT NULL,
  `Value` varchar(50) DEFAULT NULL,
  `ValueType` varchar(20) NOT NULL,
  PRIMARY KEY (`ID`),
  KEY `AnalysisID` (`AnalysisID`),
  CONSTRAINT `AnalysisCriteria_ibfk_1` FOREIGN KEY (`AnalysisID`) REFERENCES `Analysis` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `AnalysisDerivedData`
--

DROP TABLE IF EXISTS `AnalysisDerivedData`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `AnalysisDerivedData` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `AnalysisID` int(11) NOT NULL,
  `Name` varchar(50) NOT NULL,
  `Equation` varchar(200) NOT NULL,
  PRIMARY KEY (`ID`),
  KEY `AnalysisID` (`AnalysisID`),
  CONSTRAINT `AnalysisDerivedData_ibfk_1` FOREIGN KEY (`AnalysisID`) REFERENCES `Analysis` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `AnalysisGraph`
--

DROP TABLE IF EXISTS `AnalysisGraph`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `AnalysisGraph` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `AnalysisID` int(11) NOT NULL,
  `xAxisName` varchar(50) NOT NULL,
  `yAxisName` varchar(50) NOT NULL,
  `xAxisType` varchar(10) NOT NULL,
  `yAxisType` varchar(10) NOT NULL,
  `AxisValueType` varchar(12) NOT NULL,
  `Rank` int(11) DEFAULT NULL,
  `LineStyle` varchar(20) NOT NULL,
  `LineColour` varchar(10) NOT NULL,
  `LineWidth` int(11) NOT NULL,
  PRIMARY KEY (`ID`),
  KEY `AnalysisID` (`AnalysisID`),
  CONSTRAINT `AnalysisGraph_ibfk_1` FOREIGN KEY (`AnalysisID`) REFERENCES `performance_dev`.`Analysis` (`ID`),
  CONSTRAINT `AnalysisGraph_ibfk_2` FOREIGN KEY (`AnalysisID`) REFERENCES `performance_dev`.`Analysis` (`ID`),
  CONSTRAINT `AnalysisGraph_ibfk_3` FOREIGN KEY (`AnalysisID`) REFERENCES `Analysis` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `AnalysisMethod`
--

DROP TABLE IF EXISTS `AnalysisMethod`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `AnalysisMethod` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `AxisID` int(11) NOT NULL,
  `Class` varchar(200) NOT NULL,
  `RangeFrom` int(11) DEFAULT NULL,
  `RangeTo` int(11) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `AxisID` (`AxisID`),
  CONSTRAINT `AnalysisMethod_ibfk_1` FOREIGN KEY (`AxisID`) REFERENCES `AnalysisGraph` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `AnalysisSeries`
--

DROP TABLE IF EXISTS `AnalysisSeries`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `AnalysisSeries` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `AxisID` int(11) NOT NULL,
  `SeriesType` varchar(20) NOT NULL,
  `SeriesSubType` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `AxisID` (`AxisID`),
  CONSTRAINT `AnalysisSeries_ibfk_1` FOREIGN KEY (`AxisID`) REFERENCES `AnalysisGraph` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Application`
--

DROP TABLE IF EXISTS `Application`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Application` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `Name` varchar(75) NOT NULL,
  `VersionMajor` smallint(6) unsigned NOT NULL,
  `VersionMinor` smallint(6) unsigned NOT NULL,
  `VersionBuild` smallint(6) unsigned NOT NULL,
  `VersionCode` varchar(5) NOT NULL,
  `Description` varchar(300) DEFAULT NULL,
  `Private` tinyint(1) NOT NULL,
  `FullName` varchar(200) DEFAULT NULL COMMENT 'Name reported by app esp in PMTM. May not exist and may not be unique',
  PRIMARY KEY (`ID`),
  UNIQUE KEY `NaturalKey` (`Name`,`VersionMajor`,`VersionMinor`,`VersionBuild`,`VersionCode`)
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Compiler`
--

DROP TABLE IF EXISTS `Compiler`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Compiler` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `Name` char(50) NOT NULL,
  `Vendor` char(50) NOT NULL,
  `VersionMajor` int(11) NOT NULL,
  `VersionMinor` int(11) NOT NULL,
  `VersionBuild` int(11) NOT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `NaturalKey` (`Name`,`Vendor`,`VersionMajor`,`VersionMinor`,`VersionBuild`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Flags`
--

DROP TABLE IF EXISTS `Flags`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Flags` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `Flag` varchar(50) NOT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=40 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Library`
--

DROP TABLE IF EXISTS `Library`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Library` (
  `ID` int(11) NOT NULL,
  `Name` char(50) NOT NULL,
  `Vendor` char(50) NOT NULL,
  `CompiledWith` int(11) DEFAULT NULL,
  `VersionMajor` int(11) NOT NULL,
  `VersionMinor` int(11) NOT NULL,
  `VersionBuild` int(11) NOT NULL,
  PRIMARY KEY (`ID`),
  KEY `CompilerCheck` (`CompiledWith`),
  CONSTRAINT `CompilerCheck` FOREIGN KEY (`CompiledWith`) REFERENCES `Compiler` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `MPI`
--

DROP TABLE IF EXISTS `MPI`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `MPI` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `Name` char(50) NOT NULL,
  `Vendor` char(50) NOT NULL,
  `VersionMajor` int(11) NOT NULL,
  `VersionMinor` int(11) NOT NULL,
  `VersionBuild` int(11) NOT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `NaturalKey` (`Name`,`Vendor`,`VersionMajor`,`VersionMinor`,`VersionBuild`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Machine`
--

DROP TABLE IF EXISTS `Machine`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Machine` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `Name` char(40) NOT NULL,
  `Vendor` varchar(50) NOT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `NaturalKey` (`Name`,`Vendor`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `OperatingSystem`
--

DROP TABLE IF EXISTS `OperatingSystem`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `OperatingSystem` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `Name` varchar(50) NOT NULL,
  `VersionMajor` int(11) NOT NULL,
  `VersionMinor` int(11) NOT NULL,
  `VersionBuild` int(11) NOT NULL,
  `Vendor` varchar(50) NOT NULL,
  `VersionBuildMinor` int(11) NOT NULL,
  `Kernel` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `NaturalKey` (`Name`,`VersionMajor`,`VersionMinor`,`VersionBuild`,`Vendor`,`VersionBuildMinor`,`Kernel`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Parameter`
--

DROP TABLE IF EXISTS `Parameter`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Parameter` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `Name` varchar(50) NOT NULL,
  `Type` varchar(20) NOT NULL,
  `SubRunOwner` int(11) NOT NULL,
  `DoubleValue` double DEFAULT NULL,
  `IntegerValue` int(11) DEFAULT NULL,
  `StringValue` varchar(250) DEFAULT NULL,
  `Rank` int(11) DEFAULT NULL,
  `ThreadID` int(10) unsigned DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `SubRunOwner` (`SubRunOwner`),
  KEY `Name` (`Name`),
  CONSTRAINT `Parameter_ibfk_2` FOREIGN KEY (`SubRunOwner`) REFERENCES `SubRun` (`ID`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=399260 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Processor`
--

DROP TABLE IF EXISTS `Processor`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Processor` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `Vendor` char(40) NOT NULL,
  `ProcessorArchitecture` varchar(20) NOT NULL,
  `CoresPerProcessor` int(11) NOT NULL,
  `ThreadsPerCore` int(11) NOT NULL,
  `Name` varchar(100) NOT NULL,
  `ClockSpeedHz` int(11) NOT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `NaturalKey` (`Name`,`Vendor`,`ProcessorArchitecture`,`CoresPerProcessor`,`ThreadsPerCore`,`ClockSpeedHz`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Result`
--

DROP TABLE IF EXISTS `Result`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Result` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `Name` char(50) NOT NULL,
  `Value` double NOT NULL,
  `Error` double NOT NULL DEFAULT '0',
  `Rank` int(11) DEFAULT NULL,
  `ThreadID` int(11) DEFAULT NULL,
  `ErrorType` varchar(20) NOT NULL,
  `SubRun` int(11) NOT NULL,
  `Count` bigint(11) DEFAULT NULL,
  `PauseCount` bigint(11) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `Name` (`Name`),
  KEY `SubRun` (`SubRun`),
  CONSTRAINT `Result_ibfk_2` FOREIGN KEY (`SubRun`) REFERENCES `SubRun` (`ID`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1526717 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Run`
--

DROP TABLE IF EXISTS `Run`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Run` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `RunCreator` char(30) NOT NULL COMMENT 'This is the person who created the data',
  `Date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `Private` tinyint(1) NOT NULL DEFAULT '1',
  `Machine` int(11) NOT NULL,
  `OperatingSystem` int(11) NOT NULL,
  `Processor` int(11) NOT NULL,
  `Compiler` int(11) NOT NULL,
  `MPI` int(11) NOT NULL,
  `Application` int(11) NOT NULL,
  `RunDate` datetime NOT NULL DEFAULT '2000-00-00 00:00:00' COMMENT 'Time at which the run was recorded',
  `RunID` char(32) DEFAULT NULL COMMENT 'A unique identifier of the performance modelling run',
  `Tag` varchar(50) DEFAULT NULL,
  `File` varchar(500) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `RunID` (`RunID`),
  KEY `AppCheck` (`Application`),
  KEY `Machine` (`Machine`),
  KEY `OperatingSystem` (`OperatingSystem`),
  KEY `Compiler` (`Compiler`),
  KEY `MPI` (`MPI`),
  KEY `Processor` (`Processor`),
  CONSTRAINT `Run_ibfk_10` FOREIGN KEY (`Machine`) REFERENCES `Machine` (`ID`) ON DELETE CASCADE,
  CONSTRAINT `Run_ibfk_11` FOREIGN KEY (`OperatingSystem`) REFERENCES `OperatingSystem` (`ID`) ON DELETE CASCADE,
  CONSTRAINT `Run_ibfk_12` FOREIGN KEY (`Processor`) REFERENCES `Processor` (`ID`) ON DELETE CASCADE,
  CONSTRAINT `Run_ibfk_13` FOREIGN KEY (`Compiler`) REFERENCES `Compiler` (`ID`) ON DELETE CASCADE,
  CONSTRAINT `Run_ibfk_14` FOREIGN KEY (`MPI`) REFERENCES `MPI` (`ID`) ON DELETE CASCADE,
  CONSTRAINT `Run_ibfk_15` FOREIGN KEY (`Application`) REFERENCES `Application` (`ID`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=883 DEFAULT CHARSET=latin1 ROW_FORMAT=FIXED;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `RunFlags`
--

DROP TABLE IF EXISTS `RunFlags`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `RunFlags` (
  `RunID` int(11) NOT NULL,
  `FlagID` int(11) NOT NULL,
  PRIMARY KEY (`RunID`,`FlagID`),
  KEY `RunID` (`RunID`),
  KEY `FlagID` (`FlagID`),
  CONSTRAINT `RunFlags_ibfk_3` FOREIGN KEY (`RunID`) REFERENCES `Run` (`ID`) ON DELETE CASCADE,
  CONSTRAINT `RunFlags_ibfk_4` FOREIGN KEY (`FlagID`) REFERENCES `Flags` (`ID`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `SubRun`
--

DROP TABLE IF EXISTS `SubRun`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `SubRun` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `ParentRun` int(11) NOT NULL,
  `Sequence` int(11) NOT NULL,
  PRIMARY KEY (`ID`),
  KEY `ParentRun` (`ParentRun`),
  CONSTRAINT `SubRun_ibfk_2` FOREIGN KEY (`ParentRun`) REFERENCES `Run` (`ID`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=883 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2014-03-19 10:56:27
