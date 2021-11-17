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
-- Table `dindersql`.`Merkmal`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `dindersql`.`Merkmal` (
  `merkmaleID` INT NOT NULL,
  `alter` INT NULL DEFAULT NULL,
  `geschlecht` TINYINT(1) NULL DEFAULT NULL,
  `groesse` INT NULL DEFAULT NULL,
  `Tier_tierID` INT NOT NULL,
  PRIMARY KEY (`merkmaleID`),
  INDEX `fk_Merkmal_Tier1_idx` (`Tier_tierID` ASC) VISIBLE,
  CONSTRAINT `fk_Merkmal_Tier1`
    FOREIGN KEY (`Tier_tierID`)
    REFERENCES `dindersql`.`Tier` (`tierID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
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
-- Table `dindersql`.`VormerkListe`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `dindersql`.`VormerkListe` (
  `vormerkListeID` INT NOT NULL,
  PRIMARY KEY (`vormerkListeID`))
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
  `VormerkListe_vormerkListeID` INT NOT NULL,
  `LikeListe_likelisteID` INT NOT NULL,
  `Merkmal_merkmaleID` INT NOT NULL,
  PRIMARY KEY (`profilID`, `konto_email`),
  INDEX `fk_Profil_konto1_idx` (`konto_email` ASC) VISIBLE,
  INDEX `fk_Profil_VormerkListe1_idx` (`VormerkListe_vormerkListeID` ASC) VISIBLE,
  INDEX `fk_Profil_LikeListe1_idx` (`LikeListe_likelisteID` ASC) VISIBLE,
  INDEX `fk_Profil_Merkmal1_idx` (`Merkmal_merkmaleID` ASC) VISIBLE,
  CONSTRAINT `fk_Profil_konto1`
    FOREIGN KEY (`konto_email`)
    REFERENCES `dindersql`.`konto` (`email`),
  CONSTRAINT `fk_Profil_VormerkListe1`
    FOREIGN KEY (`VormerkListe_vormerkListeID`)
    REFERENCES `dindersql`.`VormerkListe` (`vormerkListeID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_Profil_LikeListe1`
    FOREIGN KEY (`LikeListe_likelisteID`)
    REFERENCES `dindersql`.`LikeListe` (`likelisteID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_Profil_Merkmal1`
    FOREIGN KEY (`Merkmal_merkmaleID`)
    REFERENCES `dindersql`.`Merkmal` (`merkmaleID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
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
-- Table `dindersql`.`Reports`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `dindersql`.`Reports` (
  `reportID` INT NOT NULL,
  `nachricht` VARCHAR(150) NOT NULL,
  `konto_email` VARCHAR(100) NOT NULL,
  `konto_email_taeter` VARCHAR(100) NOT NULL,
  `Admin_adminID` INT NOT NULL,
  PRIMARY KEY (`reportID`, `konto_email_taeter`),
  INDEX `fk_Reports_konto1_idx` (`konto_email` ASC) VISIBLE,
  INDEX `fk_Reports_konto2_idx` (`konto_email_taeter` ASC) VISIBLE,
  INDEX `fk_Reports_Admin1_idx` (`Admin_adminID` ASC) VISIBLE,
  CONSTRAINT `fk_Reports_konto1`
    FOREIGN KEY (`konto_email`)
    REFERENCES `dindersql`.`konto` (`email`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_Reports_konto2`
    FOREIGN KEY (`konto_email_taeter`)
    REFERENCES `dindersql`.`konto` (`email`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_Reports_Admin1`
    FOREIGN KEY (`Admin_adminID`)
    REFERENCES `dindersql`.`Admin` (`adminID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `dindersql`.`MatchListe`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `dindersql`.`MatchListe` (
  `matchListeID` INT NOT NULL,
  `istGeloescht` TINYINT(1) NULL,
  `Profil_profilID` INT NOT NULL,
  `Profil_konto_email` VARCHAR(100) NOT NULL,
  PRIMARY KEY (`matchListeID`),
  INDEX `fk_MatchListe_Profil1_idx` (`Profil_profilID` ASC, `Profil_konto_email` ASC) VISIBLE,
  CONSTRAINT `fk_MatchListe_Profil1`
    FOREIGN KEY (`Profil_profilID` , `Profil_konto_email`)
    REFERENCES `dindersql`.`Profil` (`profilID` , `konto_email`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `dindersql`.`FreundeBeziehung`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `dindersql`.`FreundeBeziehung` (
  `beziehungID` INT NOT NULL,
  `Profil_profilID` INT NOT NULL,
  `Profil_konto_email` VARCHAR(100) NOT NULL,
  `Profil_profilID1` INT NOT NULL,
  `Profil_konto_email1` VARCHAR(100) NOT NULL,
  PRIMARY KEY (`beziehungID`),
  INDEX `fk_FreundeBeziehung_Profil1_idx` (`Profil_profilID` ASC, `Profil_konto_email` ASC) VISIBLE,
  INDEX `fk_FreundeBeziehung_Profil2_idx` (`Profil_profilID1` ASC, `Profil_konto_email1` ASC) VISIBLE,
  CONSTRAINT `fk_FreundeBeziehung_Profil1`
    FOREIGN KEY (`Profil_profilID` , `Profil_konto_email`)
    REFERENCES `dindersql`.`Profil` (`profilID` , `konto_email`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_FreundeBeziehung_Profil2`
    FOREIGN KEY (`Profil_profilID1` , `Profil_konto_email1`)
    REFERENCES `dindersql`.`Profil` (`profilID` , `konto_email`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `dindersql`.`Nachrichten`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `dindersql`.`Nachrichten` (
  `msgID` INT NOT NULL,
  `msgText` VARCHAR(500) NULL,
  `replyTo` INT NOT NULL,
  PRIMARY KEY (`msgID`),
  INDEX `fk_Nachrichten_Nachrichten1_idx` (`replyTo` ASC) VISIBLE,
  CONSTRAINT `fk_Nachrichten_Nachrichten1`
    FOREIGN KEY (`replyTo`)
    REFERENCES `dindersql`.`Nachrichten` (`msgID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `dindersql`.`Chat`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `dindersql`.`Chat` (
  `chatID` INT NOT NULL,
  `Profil_profilID` INT NOT NULL,
  `Profil_konto_email` VARCHAR(100) NOT NULL,
  `Profil_profilID1` INT NOT NULL,
  `Profil_konto_email1` VARCHAR(100) NOT NULL,
  `Nachrichten_msgID` INT NOT NULL,
  PRIMARY KEY (`chatID`),
  INDEX `fk_Chat_Profil1_idx` (`Profil_profilID` ASC, `Profil_konto_email` ASC) VISIBLE,
  INDEX `fk_Chat_Profil2_idx` (`Profil_profilID1` ASC, `Profil_konto_email1` ASC) VISIBLE,
  INDEX `fk_Chat_Nachrichten1_idx` (`Nachrichten_msgID` ASC) VISIBLE,
  CONSTRAINT `fk_Chat_Profil1`
    FOREIGN KEY (`Profil_profilID` , `Profil_konto_email`)
    REFERENCES `dindersql`.`Profil` (`profilID` , `konto_email`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_Chat_Profil2`
    FOREIGN KEY (`Profil_profilID1` , `Profil_konto_email1`)
    REFERENCES `dindersql`.`Profil` (`profilID` , `konto_email`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_Chat_Nachrichten1`
    FOREIGN KEY (`Nachrichten_msgID`)
    REFERENCES `dindersql`.`Nachrichten` (`msgID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
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
  `Profil_profilID` INT NOT NULL,
  `Profil_konto_email` VARCHAR(100) NOT NULL,
  PRIMARY KEY (`suchfilterID`),
  INDEX `fk_Suchfilter_Profil1_idx` (`Profil_profilID` ASC, `Profil_konto_email` ASC) VISIBLE,
  CONSTRAINT `fk_Suchfilter_Profil1`
    FOREIGN KEY (`Profil_profilID` , `Profil_konto_email`)
    REFERENCES `dindersql`.`Profil` (`profilID` , `konto_email`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `dindersql`.`VormerkListe_Profil`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `dindersql`.`VormerkListe_Profil` (
  `VormerkListe_vormerkListeID` INT NOT NULL,
  `Profil_profilID` INT NOT NULL,
  `Profil_konto_email` VARCHAR(100) NOT NULL,
  INDEX `fk_VormerkListe_Profil_VormerkListe1_idx` (`VormerkListe_vormerkListeID` ASC) VISIBLE,
  INDEX `fk_VormerkListe_Profil_Profil1_idx` (`Profil_profilID` ASC, `Profil_konto_email` ASC) VISIBLE,
  CONSTRAINT `fk_VormerkListe_Profil_VormerkListe1`
    FOREIGN KEY (`VormerkListe_vormerkListeID`)
    REFERENCES `dindersql`.`VormerkListe` (`vormerkListeID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_VormerkListe_Profil_Profil1`
    FOREIGN KEY (`Profil_profilID` , `Profil_konto_email`)
    REFERENCES `dindersql`.`Profil` (`profilID` , `konto_email`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `dindersql`.`LikeListe_Profil`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `dindersql`.`LikeListe_Profil` (
  `LikeListe_likelisteID` INT NOT NULL,
  `Profil_profilID` INT NOT NULL,
  `Profil_konto_email` VARCHAR(100) NOT NULL,
  INDEX `fk_LikeListe_Profil_LikeListe1_idx` (`LikeListe_likelisteID` ASC) VISIBLE,
  INDEX `fk_LikeListe_Profil_Profil1_idx` (`Profil_profilID` ASC, `Profil_konto_email` ASC) VISIBLE,
  CONSTRAINT `fk_LikeListe_Profil_LikeListe1`
    FOREIGN KEY (`LikeListe_likelisteID`)
    REFERENCES `dindersql`.`LikeListe` (`likelisteID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_LikeListe_Profil_Profil1`
    FOREIGN KEY (`Profil_profilID` , `Profil_konto_email`)
    REFERENCES `dindersql`.`Profil` (`profilID` , `konto_email`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `dindersql`.`Profil_MatchListe`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `dindersql`.`Profil_MatchListe` (
  `MatchListe_matchListeID` INT NOT NULL,
  `Profil_profilID` INT NOT NULL,
  `Profil_konto_email` VARCHAR(100) NOT NULL,
  INDEX `fk_Profil_MatchListe_MatchListe1_idx` (`MatchListe_matchListeID` ASC) VISIBLE,
  INDEX `fk_Profil_MatchListe_Profil1_idx` (`Profil_profilID` ASC, `Profil_konto_email` ASC) VISIBLE,
  CONSTRAINT `fk_Profil_MatchListe_MatchListe1`
    FOREIGN KEY (`MatchListe_matchListeID`)
    REFERENCES `dindersql`.`MatchListe` (`matchListeID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_Profil_MatchListe_Profil1`
    FOREIGN KEY (`Profil_profilID` , `Profil_konto_email`)
    REFERENCES `dindersql`.`Profil` (`profilID` , `konto_email`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
