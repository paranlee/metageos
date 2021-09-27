## Report with websocket batch task scheduler

Excel based batch (PG based Queue) scehduler.

with websocket support - purpose of realtime chatbot API, batch monitoring are yet developing ;)

Requirements `JDK ≥ 1.8.x`, `Maven ≥ 3.x.x`.

## Build and run the app

```bash
cd spring-boot-websocket-chat-demo
mvn package
java -jar target/spring-schedule-0.0.1-SNAPSHOT.jar
```

Alternatively, you can run the app directly without packaging it like so -

```bash
mvn spring-boot:run
```

## PG as Queue table

Create Queue table.

```SQL
CREATE TABLE paran_report (
    report_seq int8 NOT NULL DEFAULT nextval('paran_report_seq'::regclass),
    report_type int4 NULL,
    usr_id varchar(100) NOT NULL,
    file_nm text NULL,
    file_path text NULL,
    log_part int4 NOT NULL,
    log_seq int4 NOT NULL,
    flag bpchar(1) NOT NULL DEFAULT 'R'::bpchar,
    cre_dtime timestamp NULL DEFAULT CURRENT_TIMESTAMP,
    end_dtime timestamp NULL,
    udt_dtime timestamp NULL DEFAULT CURRENT_TIMESTAMP,
    err text NULL,
    CONSTRAINT paran_report_pk PRIMARY KEY (report_seq)
);
```

Create each job specification.

```SQL
CREATE TABLE paran_report_type (
    -- report_type int4 NULL,
    sheet_name text NULL,
    -- sheet_seq int4 NULL,
    excel_cell text NULL,
    doing text NULL,
    query_key text NULL,
    arg text NULL,
    hidden_sheet_name text NULL,
    -- CONSTRAINT paran_report_type_uk UNIQUE (report_type, sheet_name, sheet_seq)
);
```

Insert sample dummy table.

```SQL
INSERT INTO paran_report_type
(report_type, sheet_name, sheet_seq, excel_cell, doing, query_key, arg, hidden_sheet_name)
VALUES(0, 'sheet1', 0, 'C3', 'setDataTable', 'getDummyTable', NULL, NULL);
INSERT INTO paran_report_type
(report_type, sheet_name, sheet_seq, excel_cell, doing, query_key, arg, hidden_sheet_name)
VALUES(0, 'sheet2', 0, 'F5', 'setDataTable', 'getDummyTable', NULL, NULL);
```
