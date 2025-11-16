-- departmentConfigData.sql

SET FOREIGN_KEY_CHECKS=0;

-- 기존 Unique Key가 존재한다면 삭제합니다.
ALTER TABLE department_configs DROP INDEX IF EXISTS ux_department_configs_name;

-- Unique Key를 다시 생성합니다.
ALTER TABLE department_configs
  ADD UNIQUE KEY IF NOT EXISTS ux_department_configs_name (name);

-- 2. 데이터 삽입 (멱등성 확보)

INSERT INTO department_configs (name, url, is_rss, last_modified, created_at, updated_at, status)
SELECT '학사', 'https://www.konkuk.ac.kr/bbs/konkuk/234/rssList.do?row=50', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'ACTIVE'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM department_configs WHERE name='학사');

INSERT INTO department_configs (name, url, is_rss, last_modified, created_at, updated_at, status)
SELECT '장학', 'https://www.konkuk.ac.kr/bbs/konkuk/235/rssList.do?row=50', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'ACTIVE'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM department_configs WHERE name='장학');

INSERT INTO department_configs (name, url, is_rss, last_modified, created_at, updated_at, status)
SELECT '국제', 'https://www.konkuk.ac.kr/bbs/konkuk/237/rssList.do?row=50', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'ACTIVE'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM department_configs WHERE name='국제');

INSERT INTO department_configs (name, url, is_rss, last_modified, created_at, updated_at, status)
SELECT '학생', 'https://www.konkuk.ac.kr/bbs/konkuk/238/rssList.do?row=50', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'ACTIVE'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM department_configs WHERE name='학생');

INSERT INTO department_configs (name, url, is_rss, last_modified, created_at, updated_at, status)
SELECT '일반', 'https://www.konkuk.ac.kr/bbs/konkuk/240/rssList.do?row=50', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'ACTIVE'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM department_configs WHERE name='일반');

INSERT INTO department_configs (name, url, is_rss, last_modified, created_at, updated_at, status)
SELECT '채용', 'https://www.konkuk.ac.kr/bbs/konkuk/243/rssList.do?row=50', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'ACTIVE'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM department_configs WHERE name='채용');

INSERT INTO department_configs (name, url, is_rss, last_modified, created_at, updated_at, status)
SELECT '에너지 절약', 'https://www.konkuk.ac.kr/bbs/konkuk/242/rssList.do?row=50', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'ACTIVE'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM department_configs WHERE name='에너지 절약');



INSERT INTO department_configs (name, url, is_rss, last_modified, created_at, updated_at, status)
SELECT '국어국문학과', 'https://korea.konkuk.ac.kr/korea/3834/subview.do', FALSE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'ACTIVE'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM department_configs WHERE name='국어국문학과');

INSERT INTO department_configs (name, url, is_rss, last_modified, created_at, updated_at, status)
SELECT '영어영문학과', 'https://english.konkuk.ac.kr/english/3903/subview.do', FALSE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'ACTIVE'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM department_configs WHERE name='영어영문학과');

INSERT INTO department_configs (name, url, is_rss, last_modified, created_at, updated_at, status)
SELECT '중어중문학과', 'https://china.konkuk.ac.kr/china/4002/subview.do', FALSE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'ACTIVE'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM department_configs WHERE name='중어중문학과');

INSERT INTO department_configs (name, url, is_rss, last_modified, created_at, updated_at, status)
SELECT '철학과', 'https://philo.konkuk.ac.kr/philo/4063/subview.do', FALSE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'ACTIVE'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM department_configs WHERE name='철학과');

INSERT INTO department_configs (name, url, is_rss, last_modified, created_at, updated_at, status)
SELECT '사학과', 'https://khistory.konkuk.ac.kr/khistory/4126/subview.do', FALSE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'ACTIVE'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM department_configs WHERE name='사학과');

INSERT INTO department_configs (name, url, is_rss, last_modified, created_at, updated_at, status)
SELECT '미디어커뮤니케이션학과', 'https://comm.konkuk.ac.kr/comm/7898/subview.do', FALSE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'ACTIVE'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM department_configs WHERE name='미디어커뮤니케이션학과');

INSERT INTO department_configs (name, url, is_rss, last_modified, created_at, updated_at, status)
SELECT '문화콘텐츠학과', 'https://culturecontents.konkuk.ac.kr/culturecontents/8010/subview.do', FALSE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'ACTIVE'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM department_configs WHERE name='문화콘텐츠학과');


-- 사회과학대학
INSERT INTO department_configs (name, url, is_rss, last_modified, created_at, updated_at, status)
SELECT '정치외교학과', 'https://kupol.konkuk.ac.kr/kupol/10202/subview.do', FALSE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'ACTIVE'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM department_configs WHERE name='정치외교학과');

INSERT INTO department_configs (name, url, is_rss, last_modified, created_at, updated_at, status)
SELECT '경제학과', 'https://econ.konkuk.ac.kr/econ/10235/subview.do', FALSE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'ACTIVE'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM department_configs WHERE name='경제학과');

INSERT INTO department_configs (name, url, is_rss, last_modified, created_at, updated_at, status)
SELECT '행정학과', 'https://kupa.konkuk.ac.kr/kupa/10268/subview.do', FALSE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'ACTIVE'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM department_configs WHERE name='행정학과');

INSERT INTO department_configs (name, url, is_rss, last_modified, created_at, updated_at, status)
SELECT '국제무역학과', 'https://itrade.konkuk.ac.kr/itrade/10378/subview.do', FALSE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'ACTIVE'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM department_configs WHERE name='국제무역학과');

INSERT INTO department_configs (name, url, is_rss, last_modified, created_at, updated_at, status)
SELECT '응용통계학과', 'https://stat.konkuk.ac.kr/stat/16153/subview.do', FALSE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'ACTIVE'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM department_configs WHERE name='응용통계학과');

INSERT INTO department_configs (name, url, is_rss, last_modified, created_at, updated_at, status)
SELECT '융합인재학과', 'https://dola.konkuk.ac.kr/dola/10433/subview.do', FALSE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'ACTIVE'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM department_configs WHERE name='융합인재학과');


-- 자연과학대학
INSERT INTO department_configs (name, url, is_rss, last_modified, created_at, updated_at, status)
SELECT '수학과', 'https://math.konkuk.ac.kr/math/9750/subview.do', FALSE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'ACTIVE'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM department_configs WHERE name='수학과');

INSERT INTO department_configs (name, url, is_rss, last_modified, created_at, updated_at, status)
SELECT '물리학과', 'https://phys.konkuk.ac.kr/phys/9783/subview.do', FALSE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'ACTIVE'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM department_configs WHERE name='물리학과');

INSERT INTO department_configs (name, url, is_rss, last_modified, created_at, updated_at, status)
SELECT '화학과', 'https://chemi.konkuk.ac.kr/chemi/9809/subview.do', FALSE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'ACTIVE'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM department_configs WHERE name='화학과');


-- 공과대학 (일부 예시)
INSERT INTO department_configs (name, url, is_rss, last_modified, created_at, updated_at, status)
SELECT '전기전자공학부', 'https://ee.konkuk.ac.kr/ee/10977/subview.do', FALSE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'ACTIVE'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM department_configs WHERE name='전기전자공학부');

INSERT INTO department_configs (name, url, is_rss, last_modified, created_at, updated_at, status)
SELECT '화학공학부', 'https://chemeng.konkuk.ac.kr/chemeng/9929/subview.do', FALSE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'ACTIVE'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM department_configs WHERE name='화학공학부');

INSERT INTO department_configs (name, url, is_rss, last_modified, created_at, updated_at, status)
SELECT '컴퓨터공학부', 'https://cse.konkuk.ac.kr/cse/9962/subview.do', FALSE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'ACTIVE'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM department_configs WHERE name='컴퓨터공학부');

INSERT INTO department_configs (name, url, is_rss, last_modified, created_at, updated_at, status)
SELECT '사회환경공학부', 'https://cee.konkuk.ac.kr/cee/10033/subview.do', FALSE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'ACTIVE'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM department_configs WHERE name='사회환경공학부');

INSERT INTO department_configs (name, url, is_rss, last_modified, created_at, updated_at, status)
SELECT '기계항공공학부', 'https://mae.konkuk.ac.kr/mae/9906/subview.do', FALSE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'ACTIVE'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM department_configs WHERE name='기계항공공학부');

INSERT INTO department_configs (name, url, is_rss, last_modified, created_at, updated_at, status)
SELECT '생물공학과', 'https://microbio.konkuk.ac.kr/microbio/10115/subview.do', FALSE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'ACTIVE'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM department_configs WHERE name='생물공학과');

INSERT INTO department_configs (name, url, is_rss, last_modified, created_at, updated_at, status)
SELECT '산업공학과', 'https://kies.konkuk.ac.kr/kies/9989/subview.do', FALSE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'ACTIVE'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM department_configs WHERE name='산업공학과');

-- 경영대학
INSERT INTO department_configs (name, url, is_rss, last_modified, created_at, updated_at, status)
SELECT '경영학과', 'https://biz.konkuk.ac.kr/biz/19327/subview.do', FALSE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'ACTIVE'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM department_configs WHERE name='경영학과');

INSERT INTO department_configs (name, url, is_rss, last_modified, created_at, updated_at, status)
SELECT '기술경영학과', 'https://mot.konkuk.ac.kr/mot/19328/subview.do', FALSE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'ACTIVE'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM department_configs WHERE name='기술경영학과');

-- 건축대학
INSERT INTO department_configs (name, url, is_rss, last_modified, created_at, updated_at, status)
SELECT '건축학부', 'https://caku.konkuk.ac.kr/caku/9853/subview.do', FALSE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'ACTIVE'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM department_configs WHERE name='건축학부');

-- 수의과대학
INSERT INTO department_configs (name, url, is_rss, last_modified, created_at, updated_at, status)
SELECT '수의학과', 'https://vet.konkuk.ac.kr/vet/11138/subview.do', FALSE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'ACTIVE'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM department_configs WHERE name='수의학과');


SET FOREIGN_KEY_CHECKS = 1;