INSERT INTO sales (salesid, ref, name, address, type, creationdate, modificationdate)
VALUES
(1, NULL, 'OnePay+', 'Dakar', 'RESTAURATION', NULL, NULL),
(2, 'string', 'MARKET', 'string', 'MARKET', '2025-01-02 17:57:24.808', NULL);

INSERT INTO enterprise (enterpriseid, ref, name, address, maxquota, actualquota, creationdate, modificationdate)
VALUES (1, NULL, 'azertyu', 'zertyui', 123, NULL, NULL, NULL);

INSERT INTO cashier (cashierid, ref, firstname, lastname, username, email, phonenumber, role, salesid, status, creationdate, modificationdate)
VALUES
(1, 'string', 'string', 'string', 'mdiop', 'string', 'string', 'CLIENT', 1, 'ACTIVE', '2025-01-02 14:13:25.324', NULL),
(3, 'string', 'MARKET', 'CASHIER', 'cmarket', 'stringgs@gmail.com', '778399200', 'CLIENT', 2, 'ACTIVE', '2025-01-02 17:57:57.38', NULL);

INSERT INTO client (clientid, ref, firstname, lastname, username, email, phonenumber, role, enterpriseid, status, creationdate, modificationdate)
VALUES (1, 'string', 'string', 'string', 'mndiaye', 'string', 'string', 'CLIENT', 1, 'ACTIVE', '2025-01-02 14:10:36.127', NULL);


INSERT INTO enrolledmodules (enterpriseid, enrolledmodules)
VALUES (1, 'RESTAURATION'),
       (1, 'MARKET');

INSERT INTO partnership (partnershipid, ref, enterpriseid, salesid, status, creationdate, modificationdate)
VALUES
(1, NULL, 1, 1, 'ACTIVE', NULL, NULL),
(3, 'string', 1, 2, 'ACTIVE', '2025-01-02 20:24:10.067', NULL);

INSERT INTO enterpriseconfiguration (enterpriseconfigurationid, enterpriseid, maxamountrestauration, maxamountgasstation, maxamountmarket, maxamounttelephony, enterprisepercentage, employeepercentage, creationdate, modificationdate)
VALUES (1, 1, 6000, 10, 10000, 10, 10, 10, NULL, NULL);

INSERT INTO enterpriseprofile (enterpriseprofileid, enterpriseid, ref, firstname, lastname, username, email, phonenumber, role, status, creationdate, modificationdate)
VALUES
(1, 1, NULL, 'Mouhamed', 'Ndiaye', 'ndiaye', 'ndiaye@gmail.com', '771922344', 'CLIENT', 'ACTIVE', NULL, NULL),
(2, 1, NULL, 'Mouhamed', 'NDIAYE', 'mouhamed', 'mouhamed.dev@gmail.com', '763780437', 'CLIENT', 'ACTIVE', NULL, NULL);

INSERT INTO salesconfigurations (salesconfigurationsid, salesid, maxamount, minamount, creationdate)
VALUES (1, 1, 100, 100, NULL);

INSERT INTO salesprofile (salesprofileid, ref, firstname, lastname, username, email, phonenumber, role, salesid, status, creationdate, modificationdate)
VALUES (1, NULL, 'Ousmane', 'Ndiaye', 'ousmane', 'ousmane@gmail.com', '772829282', 'SALES_FINANCE', 1, 'ACTIVE', NULL, NULL);

INSERT INTO payment (paymentid, ref, clientid, cashierid, amount, status, paymentdate, creationdate, modificationdate, module)
VALUES
(8, 'string', 1, 1, 10, 'ACTIVE', '2025-01-02 15:56:00.001', '2025-01-02 15:56:00.001', NULL, 'MARKET'),
(6, 'string', 1, 1, 3000, 'ACTIVE', '2025-01-02 15:56:00.001', '2025-01-02 15:56:00.001', NULL, 'RESTAURATION'),
(5, 'string', 1, 1, 2000, 'ACTIVE', '2025-01-02 15:56:00.001', '2025-01-02 15:56:00.001', NULL, 'RESTAURATION'),
(9, 'string', 1, 3, 2000, 'ACTIVE', '2025-01-02 15:56:00.001', '2025-01-02 15:56:00.001', NULL, 'MARKET'),
(10, 'string', 1, 1, 10, 'ACTIVE', '2025-01-02 15:56:00.001', '2025-01-02 15:56:00.001', NULL, 'MARKET'),
(11, 'string', 1, 1, 10, 'ACTIVE', '2025-01-02 15:56:00.001', '2025-01-02 15:56:00.001', NULL, 'RESTAURATION'),
(7, 'string', 1, 1, 1000, 'INACTIVE', '2025-01-02 15:56:00.001', '2025-01-02 15:56:00.001', NULL, 'RESTAURATION'),
(13, 'string', 1, 1, 100, 'ACTIVE', '2025-01-02 15:56:00.001', '2025-01-02 15:56:00.001', NULL, 'RESTAURATION'),
(12, 'string', 1, 1, 10, 'INACTIVE', '2025-01-02 15:56:00.001', '2025-01-02 15:56:00.001', NULL, 'RESTAURATION');


