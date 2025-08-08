-- create_user
DROP PROCEDURE IF EXISTS create_user;
DELIMITER $$
CREATE PROCEDURE create_user(
   IN temp_loginid VARCHAR (50),
   IN temp_password VARCHAR (50),
   IN temp_firstname VARCHAR (50),
   IN temp_lastname VARCHAR (50),
   IN temp_email VARCHAR (50),
   IN temp_office_phone_number VARCHAR (50),
   IN temp_department_id INTEGER,
   IN temp_active VARCHAR (50),
   IN temp_created_by VARCHAR (50),
   IN temp_authorized_by VARCHAR (50),
   IN temp_role_id BIGINT,
   IN temp_activated_by VARCHAR (30),
   IN temp_priority INTEGER,
   IN temp_file_visibility BOOLEAN,
   IN temp_timezone VARCHAR (64)
)
BEGIN
DECLARE last_id BIGINT DEFAULT 0;
INSERT INTO m_user(loginid, passwd, fnme, lnme, email, office_phone, dept_id, active, created_by, authorized_by, authorized_ts, file_visibility, timezone)
VALUES(temp_loginid, temp_password, temp_firstname, temp_lastname, temp_email, temp_office_phone_number, temp_department_id, temp_active, temp_created_by, temp_authorized_by, CURRENT_TIMESTAMP, temp_file_visibility, temp_timezone);

SELECT ID INTO last_id FROM m_user WHERE email = temp_email;

INSERT INTO m_user_roles(user_id, role_id, priority, date_activated, activated_by) VALUES(last_id, temp_role_id, temp_priority, CURRENT_TIMESTAMP, temp_activated_by);
END$$
DELIMITER ;

-- update_user
DROP PROCEDURE IF EXISTS update_user;
DELIMITER $$
CREATE PROCEDURE update_user(
   IN temp_id BIGINT,
   IN temp_firstname VARCHAR (50),
   IN temp_lastname VARCHAR (50),
   IN temp_office_phone_number VARCHAR (50),
   IN temp_department_id INTEGER,
   IN temp_active VARCHAR (50),
   IN temp_role_id BIGINT,
   IN temp_modified_by VARCHAR (30),
   IN temp_file_visibility BOOLEAN ,
   IN temp_timezone VARCHAR (64)
)
BEGIN 
IF temp_active <> 'A' 
THEN 
UPDATE m_user SET fnme = temp_firstname, lnme = temp_lastname, office_phone = temp_office_phone_number, dept_id = temp_department_id, active = temp_active, modified_by = temp_modified_by, modified_ts = CURRENT_TIMESTAMP, deactivated_by = temp_modified_by, file_visibility = temp_file_visibility, timezone = temp_timezone, deactivated_ts = NOW() WHERE id = temp_id;
ELSE 
UPDATE m_user SET Fnme = temp_firstname, lnme = temp_lastname, office_phone = temp_office_phone_number, dept_id = temp_department_id, active = temp_active, modified_by = temp_modified_by, file_visibility = temp_file_visibility, timezone = temp_timezone, modified_ts = CURRENT_TIMESTAMP WHERE id = temp_id;
END IF;

UPDATE m_user_roles SET role_id = temp_role_id WHERE user_id = temp_id;
END$$
DELIMITER ;

-- archive_and_purge
DROP PROCEDURE IF EXISTS archive_and_purge;
DELIMITER $$
CREATE PROCEDURE archive_and_purge ()
BEGIN 
DECLARE document VARCHAR(8);
DECLARE archive_to INT;
DECLARE purge_to INT;
DECLARE done INT DEFAULT 0;
DECLARE transactions_cursor CURSOR FOR SELECT transaction, archive_days, purge_days FROM archive_purge;  
DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = 1;

OPEN transactions_cursor;
transactions: loop
    IF done = 1 THEN
        LEAVE transactions;
    END IF;
    FETCH transactions_cursor INTO document, archive_to, purge_to;
 	INSERT INTO temp_archive_fileids SELECT file_id FROM files WHERE transaction_type = document AND date_time_received < (DATE_SUB(NOW(), INTERVAL archive_to DAY));
	INSERT INTO temp_purge_fileids SELECT file_id FROM archive_files WHERE transaction_type = document AND date_time_received < (DATE_SUB(NOW(), INTERVAL purge_to DAY));
END LOOP;
CLOSE transactions_cursor;

INSERT INTO archive_asn (SELECT * FROM asn WHERE file_id IN (SELECT F_ID FROM temp_archive_fileids));
INSERT INTO archive_wh_shipping_orders (SELECT * FROM wh_shipping_orders WHERE file_id IN (SELECT F_ID FROM temp_archive_fileids));
INSERT INTO archive_wh_stock_transfer_shipment_advice (SELECT * FROM wh_stock_transfer_shipment_advice WHERE file_id IN (SELECT F_ID FROM temp_archive_fileids));
INSERT INTO archive_wh_inventory_adjustment_advice (SELECT * FROM wh_inventory_adjustment_advice WHERE file_id IN (SELECT F_ID FROM temp_archive_fileids));
INSERT INTO archive_wh_stock_transfer_receipt_advice (SELECT * FROM wh_stock_transfer_receipt_advice WHERE file_id IN (SELECT F_ID FROM temp_archive_fileids));
INSERT INTO archive_wh_shipping_advice_transaction (SELECT * FROM wh_shipping_advice_transaction WHERE file_id IN (SELECT F_ID FROM temp_archive_fileids));

DELETE FROM asn WHERE file_id IN (SELECT F_ID FROM temp_archive_fileids);
DELETE FROM wh_shipping_orders WHERE file_id IN (SELECT F_ID FROM temp_archive_fileids);
DELETE FROM wh_stock_transfer_shipment_advice WHERE file_id IN (SELECT F_ID FROM temp_archive_fileids);
DELETE FROM wh_inventory_adjustment_advice WHERE file_id IN (SELECT F_ID FROM temp_archive_fileids);
DELETE FROM wh_stock_transfer_receipt_advice WHERE file_id IN (SELECT F_ID FROM temp_archive_fileids);
DELETE FROM wh_shipping_advice_transaction WHERE file_id IN (SELECT F_ID FROM temp_archive_fileids);

INSERT INTO archive_files (SELECT * FROM files WHERE file_id IN (SELECT F_ID FROM temp_archive_fileids));
DELETE FROM files WHERE file_id IN (SELECT F_ID FROM temp_archive_fileids);
DELETE FROM temp_archive_fileids;

DELETE FROM archive_asn WHERE file_id IN (SELECT F_ID FROM temp_purge_fileids);
DELETE FROM archive_wh_shipping_orders WHERE file_id IN (SELECT F_ID FROM temp_purge_fileids);
DELETE FROM archive_wh_stock_transfer_shipment_advice WHERE file_id IN (SELECT F_ID FROM temp_purge_fileids);
DELETE FROM archive_wh_inventory_adjustment_advice WHERE file_id IN (SELECT F_ID FROM temp_purge_fileids);
DELETE FROM archive_wh_stock_transfer_receipt_advice WHERE file_id IN (SELECT F_ID FROM temp_purge_fileids);
DELETE FROM archive_wh_shipping_advice_transaction WHERE file_id IN (SELECT F_ID FROM temp_purge_fileids);
DELETE FROM archive_files WHERE file_id IN (SELECT F_ID FROM temp_purge_fileids);
DELETE FROM temp_purge_fileids;
END$$
DELIMITER ;

-- delete_user
DROP PROCEDURE IF EXISTS delete_user;
DELIMITER $$
CREATE PROCEDURE delete_user(
IN temp_user_id BIGINT 
)
BEGIN	
DELETE FROM m_user WHERE id=temp_user_id;
DELETE FROM m_user_roles WHERE user_id=temp_user_id;
DELETE FROM m_user_flows_action WHERE user_id=temp_user_id;
DELETE FROM partner_visibilty WHERE user_id=temp_user_id;
DELETE FROM sfg_partner_visibilty WHERE user_id=temp_user_id;
END$$
DELIMITER ;