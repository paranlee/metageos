--
-- PostgreSQL database dump
--

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- Name: paranlee; Type: SCHEMA; Schema: -; Owner: paranlee
--

CREATE SCHEMA paranlee_main;


ALTER SCHEMA paranlee OWNER TO paranlee;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: SAMPLE_TABLE; Type: TABLE; Schema: paranlee; Owner: paranlee
--

CREATE TABLE paranlee.SAMPLE_TABLE (
    column1 character varying,
    column2 character varying,
    column3 character varying
);


ALTER TABLE paranlee.SAMPLE_TABLE OWNER TO paranlee;

--
-- Data for Name: SAMPLE_TABLE; Type: TABLE DATA; Schema: paranlee; Owner: paranlee
--

COPY paranlee.SAMPLE_TABLE (column1, column2, column3) FROM stdin;
1	\N	4
\N	2	\N
5	\N	3
\.


--
-- PostgreSQL database dump complete
--

