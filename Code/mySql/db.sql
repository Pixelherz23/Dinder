-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema mydb
-- -----------------------------------------------------
-- -----------------------------------------------------
-- Schema dindersql
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema dindersql
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `dindersql` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci ;
USE `dindersql` ;

-- -----------------------------------------------------
-- Table `dindersql`.`Tier`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `dindersql`.`Tier` (
  `tierID` INT NOT NULL,
  `spezies` VARCHAR(45) NULL DEFAULT NULL,
  `rasse` VARCHAR(45) NULL DEFAULT NULL,
  PRIMARY KEY (`tierID`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `dindersql`.`Merkmale`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `dindersql`.`Merkmale` (
  `merkmaleID` INT NOT NULL,
  `alter` INT NULL DEFAULT NULL,
  `geschlecht` TINYINT(1) NULL DEFAULT NULL,
  `groesse` INT NULL DEFAULT NULL,
  `Tier_tierID` INT NOT NULL,
  PRIMARY KEY (`merkmaleID`, `Tier_tierID`),
  INDEX `fk_Merkmale_Tier1_idx` (`Tier_tierID` ASC) VISIBLE,
  CONSTRAINT `fk_Merkmale_Tier1`
    FOREIGN KEY (`Tier_tierID`)
    REFERENCES `dindersql`.`Tier` (`tierID`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `dindersql`.`konto`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `dindersql`.`konto` (
  `email` VARCHAR(100) NOT NULL,
  `nachname` VARCHAR(45) NOT NULL,
  `vorname` VARCHAR(45) NOT NULL,
  `telefon` INT NULL DEFAULT NULL,
  `geburtstag` DATE NOT NULL,
  `institution` VARCHAR(45) NOT NULL,
  `istOnline` TINYINT(1) NOT NULL,
  `inaktiv` TINYINT(1) NOT NULL,
  `istGesperrt` TINYINT(1) NOT NULL,
  `standort` GEOMETRY NULL DEFAULT NULL,
  PRIMARY KEY (`email`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `dindersql`.`Profil`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `dindersql`.`Profil` (
  `profilID` INT NOT NULL,
  `profilName` VARCHAR(45) NOT NULL,
  `profilbild` VARCHAR(50) NULL DEFAULT NULL,
  `beschreibung` VARCHAR(100) NULL DEFAULT NULL,
  `bewertungPositiv` BIGINT NULL DEFAULT NULL,
  `bewertungNegativ` BIGINT NULL DEFAULT NULL,
  `konto_email` VARCHAR(100) NOT NULL,
  PRIMARY KEY (`profilID`, `konto_email`),
  INDEX `fk_Profil_konto1_idx` (`konto_email` ASC) VISIBLE,
  CONSTRAINT `fk_Profil_konto1`
    FOREIGN KEY (`konto_email`)
    REFERENCES `dindersql`.`konto` (`email`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `dindersql`.`Reports`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `dindersql`.`Reports` (
  `reportID` INT NOT NULL,
  `nachricht` VARCHAR(150) NOT NULL,
  `konto_email` VARCHAR(100) NOT NULL,
  `konto_email_Verdaechtiger` VARCHAR(100) NOT NULL,
  PRIMARY KEY (`reportID`, `konto_email_Verdaechtiger`),
  INDEX `fk_reports_konto_idx` (`konto_email` ASC) VISIBLE,
  INDEX `fk_reports_konto1_idx` (`konto_email_Verdaechtiger` ASC) VISIBLE,
  CONSTRAINT `fk_reports_konto`
    FOREIGN KEY (`konto_email`)
    REFERENCES `dindersql`.`konto` (`email`),
  CONSTRAINT `fk_reports_konto1`
    FOREIGN KEY (`konto_email_Verdaechtiger`)
    REFERENCES `dindersql`.`konto` (`email`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `dindersql`.`Admin`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `dindersql`.`Admin` (
  `adminID` INT NOT NULL,
  `adminEmail` VARCHAR(45) NULL,
  `vorname` VARCHAR(45) NULL,
  `nachname` VARCHAR(45) NULL,
  `adminPasswort` VARCHAR(45) NULL,
  PRIMARY KEY (`adminID`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `dindersql`.`MatchListe`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `dindersql`.`MatchListe` (
  `matchListeID` INT NOT NULL,
  `istGeloescht` TINYINT(1) NULL,
  PRIMARY KEY (`matchListeID`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `dindersql`.`FreundeBeziehung`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `dindersql`.`FreundeBeziehung` (
  `beziehungID` INT NOT NULL,
  PRIMARY KEY (`beziehungID`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `dindersql`.`LikeListe`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `dindersql`.`LikeListe` (
  `likelisteID` INT NOT NULL,
  `profilSichtbar` TINYINT(1) NULL,
  PRIMARY KEY (`likelisteID`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `dindersql`.`Chat`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `dindersql`.`Chat` (
  `chatID` INT NOT NULL,
  PRIMARY KEY (`chatID`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `dindersql`.`Suchfilter`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `dindersql`.`Suchfilter` (
  `suchfilterID` INT NOT NULL,
  `alter` VARCHAR(45) NULL,
  `geschlecht` VARCHAR(45) NULL,
  `rasse` VARCHAR(45) NULL,
  `spezies` VARCHAR(45) NULL,
  `umkreis` INT NULL,
  PRIMARY KEY (`suchfilterID`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `dindersql`.`Nachrichten`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `dindersql`.`Nachrichten` (
  `msgID` INT NOT NULL,
  `replyTo` INT NULL,
  `msgText` VARCHAR(500) NULL,
  PRIMARY KEY (`msgID`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `dindersql`.`VormerkListe`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `dindersql`.`VormerkListe` (
  `vormerkListeID` INT NOT NULL,
  PRIMARY KEY (`vormerkListeID`))
ENGINE = InnoDB;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
