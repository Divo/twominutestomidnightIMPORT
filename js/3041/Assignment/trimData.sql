ALTER TABLE cargo DROP CONSTRAINT fk_coloumn_cargo;

DROP TABLE docket;
DROP TABLE address;
DROP TABLE haz;
DROP TABLE driver;
DROP TABLE cargo;
DROP TABLE truck;



CREATE TABLE address(
	ID INT,
	Street varchar(200) NULL,
  	City varchar(30) NULL,
  	County varchar(20) NULL,
  	Site char(1) NULL,
	weight INT NOT NULL,
	PRIMARY KEY (ID)
)
;


CREATE TABLE haz(
	ID INT,
	Name varchar (30) NULL,
	UN_Number varchar (6) NULL,
	Packing_Group varchar(3) NULL,
	Primary_Class varchar (5) NULL,
	Secondary_Class varchar (5) NULL,
	Tunnel_code varchar (7) NULL,
	PRIMARY KEY (ID)
)
;

CREATE TABLE driver(
	ID INT,
	FName varchar (15) NULL,
	SNname varchar (15) NULL,
	Residence INT NULL,
	ADR char(1) NULL,
	SafePass char(1) NULL,
	PRIMARY KEY (ID)
)
;

CREATE TABLE cargo(
	Seal varchar(10),
	Description varchar(200) NULL,
	Return_Empty char(1) NULL,
	Weight INT NULL,
	Size_ varchar(20) NULL,
	Haz INT NULL, 
	Crane char(1) NULL,
	PRIMARY KEY (Seal),
	Constraint fk_coloumn_cargo
	FOREIGN KEY(Haz) References haz
)
;

CREATE TABLE truck(
   Reg varchar(10),
   Crane char(1) NULL,  /* Is the truck equipped to control the special trailer with a crane attatched to it */
   ADR char(1) NULL,    /* Is the truck certified to haul hazardous goods */
   Road char(1) NULL,   /* Is the truck road legal or not (ie, just used for shunting around the yard */
   Last_test Date NULL,
   PRIMARY KEY (Reg)
)
;

CREATE TABLE docket(
	Docket_Number INT,
	Equipment varchar (30) NULL,
	Customer varchar(30) NULL,
	Seal varchar (20) NULL,
	Deliver_to INT NULL,
	Collect_from INT NULL,
	Haz INT NULL,
	Date_ DATE NOT NULL,
   	Driver_ INT NULL,
   	Truck varchar(10) NULL,
	PRIMARY KEY (Docket_Number),
	Constraint fk_column
	FOREIGN KEY(Deliver_to) References address,
	FOREIGN KEY(Collect_from) References address,
	FOREIGN KEY(Haz) References haz,
	FOREIGN KEY(Driver_) References driver,
	FOREIGN KEY(Truck) References truck,
	FOREIGN KEY(Seal) References cargo
) 
;
insert into truck (Reg, Crane, ADR, Road, Last_test) values('00D3452', 'N', 'N', 'Y', '21-feb-09');
insert into truck (Reg, Crane, ADR, Road, Last_test) values('05D3122', 'N', 'Y', 'Y', '21-feb-09');
insert into truck (Reg, Crane, ADR, Road, Last_test) values('05D4738', 'Y', 'N', 'Y', '21-feb-09');
