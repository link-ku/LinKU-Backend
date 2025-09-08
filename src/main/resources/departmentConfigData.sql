-- 외래 키 제약 조건 비활성화
SET FOREIGN_KEY_CHECKS = 0;

-- 데이터 삭제 및 AUTO-INCREMENT 초기화
TRUNCATE TABLE department_configs;

-- 외래 키 제약 조건 활성화
SET FOREIGN_KEY_CHECKS = 1;

insert into department_configs(department_config_id, name, url, is_rss, last_modified, created_at, updated_at, status)
values (1, '학사', 'https://www.konkuk.ac.kr/bbs/konkuk/234/rssList.do?row=50', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'ACTIVE'),
       (2, '장학', 'https://www.konkuk.ac.kr/bbs/konkuk/235/rssList.do?row=50', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'ACTIVE'),
       (3, '국제', 'https://www.konkuk.ac.kr/bbs/konkuk/237/rssList.do?row=50', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'ACTIVE');