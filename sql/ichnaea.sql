-- phpMyAdmin SQL Dump
-- version 3.5.1
-- http://www.phpmyadmin.net
--
-- Host: sql.drcs.awe.co.uk
-- Generation Time: Aug 13, 2013 at 01:57 PM
-- Server version: 5.1.54-rel12.5
-- PHP Version: 5.3.19

SET SQL_MODE="NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Database: `ichnaea`
--

-- --------------------------------------------------------

--
-- Table structure for table `Analysis`
--

CREATE TABLE IF NOT EXISTS `Analysis` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `Creator` varchar(50) NOT NULL,
  `Notes` varchar(200) DEFAULT NULL,
  `Private` tinyint(1) NOT NULL DEFAULT '1',
  `Date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `AnalysisAxis`
--

CREATE TABLE IF NOT EXISTS `AnalysisAxis` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `AnalysisID` int(11) NOT NULL,
  `xAxis` int(11) NOT NULL,
  `yAxis` int(11) NOT NULL,
  `yAxisType` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `AnalysisAxisLabel`
--

CREATE TABLE IF NOT EXISTS `AnalysisAxisLabel` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `AnalysisID` int(11) NOT NULL,
  `OldText` varchar(200) NOT NULL,
  `NewText` varchar(200) NOT NULL,
  PRIMARY KEY (`ID`),
  KEY `AnalysisID` (`AnalysisID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `AnalysisCriteria`
--

CREATE TABLE IF NOT EXISTS `AnalysisCriteria` (
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
  KEY `AnalysisID` (`AnalysisID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `AnalysisDerivedData`
--

CREATE TABLE IF NOT EXISTS `AnalysisDerivedData` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `AnalysisID` int(11) NOT NULL,
  `Name` varchar(50) NOT NULL,
  `Equation` varchar(200) NOT NULL,
  PRIMARY KEY (`ID`),
  KEY `AnalysisID` (`AnalysisID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `AnalysisGraph`
--

CREATE TABLE IF NOT EXISTS `AnalysisGraph` (
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
  KEY `AnalysisID` (`AnalysisID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `AnalysisMethod`
--

CREATE TABLE IF NOT EXISTS `AnalysisMethod` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `AxisID` int(11) NOT NULL,
  `Class` varchar(200) NOT NULL,
  `RangeFrom` int(11) DEFAULT NULL,
  `RangeTo` int(11) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `AxisID` (`AxisID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `AnalysisSeries`
--

CREATE TABLE IF NOT EXISTS `AnalysisSeries` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `AxisID` int(11) NOT NULL,
  `SeriesType` varchar(20) NOT NULL,
  `SeriesSubType` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `AxisID` (`AxisID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `Application`
--

CREATE TABLE IF NOT EXISTS `Application` (
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
) ENGINE=InnoDB DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `Compiler`
--

CREATE TABLE IF NOT EXISTS `Compiler` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `Name` char(50) NOT NULL,
  `Vendor` char(50) NOT NULL,
  `VersionMajor` int(11) NOT NULL,
  `VersionMinor` int(11) NOT NULL,
  `VersionBuild` int(11) NOT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `NaturalKey` (`Name`,`Vendor`,`VersionMajor`,`VersionMinor`,`VersionBuild`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `Flags`
--

CREATE TABLE IF NOT EXISTS `Flags` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `Flag` varchar(50) NOT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `Library`
--

CREATE TABLE IF NOT EXISTS `Library` (
  `ID` int(11) NOT NULL,
  `Name` char(50) NOT NULL,
  `Vendor` char(50) NOT NULL,
  `CompiledWith` int(11) DEFAULT NULL,
  `VersionMajor` int(11) NOT NULL,
  `VersionMinor` int(11) NOT NULL,
  `VersionBuild` int(11) NOT NULL,
  PRIMARY KEY (`ID`),
  KEY `CompilerCheck` (`CompiledWith`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `Machine`
--

CREATE TABLE IF NOT EXISTS `Machine` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `Name` char(40) NOT NULL,
  `Vendor` varchar(50) NOT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `NaturalKey` (`Name`,`Vendor`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `MPI`
--

CREATE TABLE IF NOT EXISTS `MPI` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `Name` char(50) NOT NULL,
  `Vendor` char(50) NOT NULL,
  `VersionMajor` int(11) NOT NULL,
  `VersionMinor` int(11) NOT NULL,
  `VersionBuild` int(11) NOT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `NaturalKey` (`Name`,`Vendor`,`VersionMajor`,`VersionMinor`,`VersionBuild`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `OperatingSystem`
--

CREATE TABLE IF NOT EXISTS `OperatingSystem` (
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
) ENGINE=InnoDB DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `Parameter`
--

CREATE TABLE IF NOT EXISTS `Parameter` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `Name` varchar(50) NOT NULL,
  `Type` varchar(20) NOT NULL,
  `SubRunOwner` int(11) NOT NULL,
  `DoubleValue` double DEFAULT NULL,
  `IntegerValue` int(11) DEFAULT NULL,
  `StringValue` varchar(250) DEFAULT NULL,
  `Rank` int(11) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `SubRunOwner` (`SubRunOwner`),
  KEY `Name` (`Name`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `Processor`
--

CREATE TABLE IF NOT EXISTS `Processor` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `Vendor` char(40) NOT NULL,
  `ProcessorArchitecture` varchar(20) NOT NULL,
  `CoresPerProcessor` int(11) NOT NULL,
  `ThreadsPerCore` int(11) NOT NULL,
  `Name` varchar(100) NOT NULL,
  `ClockSpeedHz` int(11) NOT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `NaturalKey` (`Name`,`Vendor`,`ProcessorArchitecture`,`CoresPerProcessor`,`ThreadsPerCore`,`ClockSpeedHz`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `Result`
--

CREATE TABLE IF NOT EXISTS `Result` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `Name` char(50) NOT NULL,
  `Value` double NOT NULL,
  `Error` double NOT NULL DEFAULT '0',
  `Rank` int(11) DEFAULT NULL,
  `ErrorType` varchar(20) NOT NULL,
  `SubRun` int(11) NOT NULL,
  `Count` bigint(11) DEFAULT NULL,
  `PauseCount` bigint(11) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `Name` (`Name`),
  KEY `SubRun` (`SubRun`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `Run`
--

CREATE TABLE IF NOT EXISTS `Run` (
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
  KEY `Processor` (`Processor`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 ROW_FORMAT=FIXED AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `RunFlags`
--

CREATE TABLE IF NOT EXISTS `RunFlags` (
  `RunID` int(11) NOT NULL,
  `FlagID` int(11) NOT NULL,
  PRIMARY KEY (`RunID`,`FlagID`),
  KEY `RunID` (`RunID`),
  KEY `FlagID` (`FlagID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `SubRun`
--

CREATE TABLE IF NOT EXISTS `SubRun` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `ParentRun` int(11) NOT NULL,
  `Sequence` int(11) NOT NULL,
  PRIMARY KEY (`ID`),
  KEY `ParentRun` (`ParentRun`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `AnalysisAxisLabel`
--
ALTER TABLE `AnalysisAxisLabel`
  ADD CONSTRAINT `AnalysisAxisLabel_ibfk_1` FOREIGN KEY (`AnalysisID`) REFERENCES `Analysis` (`ID`);

--
-- Constraints for table `AnalysisCriteria`
--
ALTER TABLE `AnalysisCriteria`
  ADD CONSTRAINT `AnalysisCriteria_ibfk_1` FOREIGN KEY (`AnalysisID`) REFERENCES `Analysis` (`ID`);

--
-- Constraints for table `AnalysisDerivedData`
--
ALTER TABLE `AnalysisDerivedData`
  ADD CONSTRAINT `AnalysisDerivedData_ibfk_1` FOREIGN KEY (`AnalysisID`) REFERENCES `Analysis` (`ID`);

--
-- Constraints for table `AnalysisGraph`
--
ALTER TABLE `AnalysisGraph`
  ADD CONSTRAINT `AnalysisGraph_ibfk_1` FOREIGN KEY (`AnalysisID`) REFERENCES `performance_dev`.`Analysis` (`ID`),
  ADD CONSTRAINT `AnalysisGraph_ibfk_2` FOREIGN KEY (`AnalysisID`) REFERENCES `performance_dev`.`Analysis` (`ID`),
  ADD CONSTRAINT `AnalysisGraph_ibfk_3` FOREIGN KEY (`AnalysisID`) REFERENCES `Analysis` (`ID`);

--
-- Constraints for table `AnalysisMethod`
--
ALTER TABLE `AnalysisMethod`
  ADD CONSTRAINT `AnalysisMethod_ibfk_1` FOREIGN KEY (`AxisID`) REFERENCES `AnalysisGraph` (`ID`);

--
-- Constraints for table `AnalysisSeries`
--
ALTER TABLE `AnalysisSeries`
  ADD CONSTRAINT `AnalysisSeries_ibfk_1` FOREIGN KEY (`AxisID`) REFERENCES `AnalysisGraph` (`ID`);

--
-- Constraints for table `Library`
--
ALTER TABLE `Library`
  ADD CONSTRAINT `CompilerCheck` FOREIGN KEY (`CompiledWith`) REFERENCES `Compiler` (`ID`);

--
-- Constraints for table `Parameter`
--
ALTER TABLE `Parameter`
  ADD CONSTRAINT `Parameter_ibfk_2` FOREIGN KEY (`SubRunOwner`) REFERENCES `SubRun` (`ID`) ON DELETE CASCADE;

--
-- Constraints for table `Result`
--
ALTER TABLE `Result`
  ADD CONSTRAINT `Result_ibfk_2` FOREIGN KEY (`SubRun`) REFERENCES `SubRun` (`ID`) ON DELETE CASCADE;

--
-- Constraints for table `Run`
--
ALTER TABLE `Run`
  ADD CONSTRAINT `Run_ibfk_10` FOREIGN KEY (`Machine`) REFERENCES `Machine` (`ID`) ON DELETE CASCADE,
  ADD CONSTRAINT `Run_ibfk_11` FOREIGN KEY (`OperatingSystem`) REFERENCES `OperatingSystem` (`ID`) ON DELETE CASCADE,
  ADD CONSTRAINT `Run_ibfk_12` FOREIGN KEY (`Processor`) REFERENCES `Processor` (`ID`) ON DELETE CASCADE,
  ADD CONSTRAINT `Run_ibfk_13` FOREIGN KEY (`Compiler`) REFERENCES `Compiler` (`ID`) ON DELETE CASCADE,
  ADD CONSTRAINT `Run_ibfk_14` FOREIGN KEY (`MPI`) REFERENCES `MPI` (`ID`) ON DELETE CASCADE,
  ADD CONSTRAINT `Run_ibfk_15` FOREIGN KEY (`Application`) REFERENCES `Application` (`ID`) ON DELETE CASCADE;

--
-- Constraints for table `RunFlags`
--
ALTER TABLE `RunFlags`
  ADD CONSTRAINT `RunFlags_ibfk_3` FOREIGN KEY (`RunID`) REFERENCES `Run` (`ID`) ON DELETE CASCADE,
  ADD CONSTRAINT `RunFlags_ibfk_4` FOREIGN KEY (`FlagID`) REFERENCES `Flags` (`ID`) ON DELETE CASCADE;

--
-- Constraints for table `SubRun`
--
ALTER TABLE `SubRun`
  ADD CONSTRAINT `SubRun_ibfk_2` FOREIGN KEY (`ParentRun`) REFERENCES `Run` (`ID`) ON DELETE CASCADE;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
