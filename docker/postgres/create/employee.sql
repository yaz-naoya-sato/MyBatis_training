/*
 【DB】
 論理名：社員名簿
 物理名：company_directory
 【テーブル】
 論理名：社員
 物理名：employee
*/
CREATE TABLE employee(
	id SERIAL NOT NULL PRIMARY KEY,                 -- ID
	employee_id CHAR(10) NOT NULL UNIQUE,			-- 社員ID
	family_name VARCHAR(20) NOT NULL,				-- 社員名（姓）
	first_name VARCHAR(20) NOT NULL,				-- 社員名（名）
	section_id NUMERIC(1) NOT NULL,					-- 所属セクション
	mail VARCHAR(256) NOT NULL,						-- メールアドレス
	gender_id NUMERIC(1) NOT NULL					-- 性別
);