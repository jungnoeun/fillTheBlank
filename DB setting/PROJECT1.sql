CREATE TABLE idle_space_analysis (
    space_id              NUMBER PRIMARY KEY,
    area_code             VARCHAR2(10),
    idle_space_cnt        NUMBER,
    culture_facility_cnt  NUMBER,
    welfare_facility_cnt  NUMBER,
    commercial_area_cnt   NUMBER,
    total_pop             NUMBER,
    youth_pop             NUMBER,
    elder_pop             NUMBER,
    avg_life_pop          NUMBER(12,3),
    avg_youth_life_pop    NUMBER(12,3),
    avg_elder_life_pop    NUMBER(12,3)
);

ALTER TABLE idle_space_analysis
MODIFY (
    avg_life_pop       NUMBER,
    avg_youth_life_pop NUMBER,
    avg_elder_life_pop NUMBER
);

DESC idle_space_analysis;


CREATE TABLE idle_space (
    idle_id              NUMBER PRIMARY KEY,
    area_code             VARCHAR2(10),
    facility_name        NUMBER,
    address  NUMBER,
    welfare_facility_cnt  NUMBER,
    commercial_area_cnt   NUMBER,
    total_pop             NUMBER,
    youth_pop             NUMBER,
    elder_pop             NUMBER,
    avg_life_pop          NUMBER(12,3),
    avg_youth_life_pop    NUMBER(12,3),
    avg_elder_life_pop    NUMBER(12,3)
);


CREATE TABLE idle_space (
    idle_id         NUMBER PRIMARY KEY,
    area_code       VARCHAR2(10),
    facility_name   VARCHAR2(100),
    address         VARCHAR2(300),
    facility_size   NUMBER,
    latitude        NUMBER,
    longtitude      NUMBER,
    area_name       VARCHAR2(50)
);



SELECT * FROM V$VERSION;



SELECT
    COUNT(*) AS total,
    COUNT(latitude) AS latitude_count,
    COUNT(longtitude) AS longitude_count
FROM idle_space;



CREATE TABLE recommendation_score (
    recommendation_id    NUMBER PRIMARY KEY,
    idle_id               NUMBER NOT NULL,
    recommendation_type   VARCHAR2(50) NOT NULL,
    score                 NUMBER(5,2) NOT NULL,
    recommendation_rank   NUMBER NOT NULL,

    CONSTRAINT fk_recommendation_idle
        FOREIGN KEY (idle_id)
        REFERENCES idle_space(idle_id)
);

DROP TABLE recommendation_score;


CREATE TABLE recommendation_score (
    recommendation_id    NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    idle_id              NUMBER NOT NULL,
    recommendation_type  VARCHAR2(50) NOT NULL,
    score                NUMBER(5,2) NOT NULL,
    recommendation_rank  NUMBER NOT NULL,

    CONSTRAINT fk_recommendation_idle
        FOREIGN KEY (idle_id)
        REFERENCES idle_space(idle_id)
);
