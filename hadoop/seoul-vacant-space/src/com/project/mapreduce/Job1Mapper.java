package com.project.mapreduce;

import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;

public class Job1Mapper
        extends Mapper<LongWritable, Text, Text, Text> {

    private final Text outputKey = new Text();
    private final Text outputValue = new Text();

    @Override
    protected void map(
            LongWritable key,
            Text value,
            Context context)
            throws IOException, InterruptedException {

        String line = value.toString();

        // 빈 줄 제거
        if (line.trim().isEmpty()) {
            return;
        }

        /*
         * =====================================================
         * 현재 Mapper가 읽고 있는 파일 이름 확인
         * =====================================================
         */
        FileSplit fileSplit =
                (FileSplit) context.getInputSplit();

        String fileName =
                fileSplit.getPath().getName();


        /*
         * =====================================================
         * CSV 파싱
         * =====================================================
         *
         * 단순히
         *
         * line.split(",")
         *
         * 를 사용하면
         *
         * "10,876"
         *
         * 같은 값이
         *
         * "10
         * 876"
         *
         * 로 잘못 분리된다.
         *
         * 따라서 따옴표 안의 쉼표는 무시하는
         * CSV parser를 사용한다.
         */
        String[] fields = parseCsvLine(line);


        /*
         * =====================================================
         * 1. 유휴공간
         * =====================================================
         *
         * vacantFacility.csv
         *
         * index 0 : 순서
         * index 1 : 행정동코드
         * index 2 : 시설명
         * index 3 : 주소
         * index 4 : 시설면적(m2)
         * index 5 : 위도
         * index 6 : 경도
         * index 7 : 행정동명
         */
        if (fileName.equals("vacantFacility.csv")) {

            // 헤더 제거
            if (fields.length > 0
                    && fields[0].equals("순서")) {
                return;
            }

            if (fields.length < 8) {
                return;
            }

            /*
             * 행정동코드
             *
             * 유휴공간은 10자리
             */
            String districtCode =
                    normalizeDistrictCode(fields[1]);

            if (districtCode.isEmpty()) {
                return;
            }

            String facilityName =
                    fields[2].trim();

            String address =
                    fields[3].trim();

            String area =
                    fields[4].trim();

            String latitude =
                    fields[5].trim();

            String longitude =
                    fields[6].trim();

            String districtName =
                    fields[7].trim();


            /*
             * 유휴공간은 나중에 실제 추천 대상이 되므로
             * 시설 상세정보를 Reducer로 전달한다.
             *
             * VACANT
             * |시설명
             * |주소
             * |면적
             * |위도
             * |경도
             * |행정동명
             */
            outputKey.set(districtCode);

            outputValue.set(
                    "VACANT|"
                    + facilityName
                    + "|"
                    + address
                    + "|"
                    + area
                    + "|"
                    + latitude
                    + "|"
                    + longitude
                    + "|"
                    + districtName
            );

            context.write(
                    outputKey,
                    outputValue
            );
        }


        /*
         * =====================================================
         * 2. 문화시설
         * =====================================================
         *
         * cultureFacility.csv
         *
         * index 0 : 번호
         * index 1 : 주제분류
         * index 2 : 문화시설명
         * index 3 : 주소
         * index 4 : 자치구
         * index 5 : 위도
         * index 6 : 경도
         * index 7 : 도로명주소
         * index 8 : 지번주소
         * index 9 : 행정동코드
         * index 10 : 행정동명
         *
         * 실제 데이터:
         *
         * 11140615
         *
         * 8자리이므로
         *
         * 1114061500
         *
         * 으로 변환한다.
         */
        else if (fileName.equals("cultureFacility.csv")) {

            // 헤더 제거
            if (fields.length > 0
                    && fields[0].equals("번호")) {
                return;
            }

            if (fields.length < 11) {
                return;
            }

            String districtCode =
                    normalizeDistrictCode(fields[9]);

            if (districtCode.isEmpty()) {
                return;
            }

            String districtName =
                    fields[10].trim();


            /*
             * 문화시설 1개
             *
             * CULTURE
             * |행정동명
             * |1
             */
            outputKey.set(districtCode);

            outputValue.set(
                    "CULTURE|"
                    + districtName
                    + "|1"
            );

            context.write(
                    outputKey,
                    outputValue
            );
        }


        /*
         * =====================================================
         * 3. 복지시설
         * =====================================================
         *
         * welfareFacility.csv
         *
         * index 0 : 순서
         * index 1 : 시설 이름
         * index 2 : 행정동명(기존)
         * index 3 : 주소
         * index 4 : 도로명주소
         * index 5 : 지번주소
         * index 6 : 행정동코드
         * index 7 : 행정동명
         * index 8 : 위도
         * index 9 : 경도
         *
         * 실제 행정동코드:
         *
         * 1129072500
         *
         * 10자리
         */
        else if (fileName.equals("welfareFacility.csv")) {

            // 헤더 제거
            if (fields.length > 0
                    && fields[0].equals("순서")) {
                return;
            }

            if (fields.length < 10) {
                return;
            }

            String districtCode =
                    normalizeDistrictCode(fields[6]);

            if (districtCode.isEmpty()) {
                return;
            }

            String districtName =
                    fields[7].trim();


            /*
             * 복지시설 1개
             *
             * WELFARE
             * |행정동명
             * |1
             */
            outputKey.set(districtCode);

            outputValue.set(
                    "WELFARE|"
                    + districtName
                    + "|1"
            );

            context.write(
                    outputKey,
                    outputValue
            );
        }


        /*
         * =====================================================
         * 4. 상권
         * =====================================================
         *
         * commercialFacility.csv
         *
         * index 0 : 순서
         * index 1 : 상권_구분_코드_명
         * index 2 : 상권_코드
         * index 3 : 상권명
         * index 4 : 엑스좌표_값
         * index 5 : 와이좌표_값
         * index 6 : 행정동코드
         * index 7 : 행정동명
         * index 8 : 영역_면적
         * index 9 : 좌표계
         *
         * 실제 데이터:
         *
         * 11140670
         * 11110515
         *
         * 8자리이므로 10자리로 변환한다.
         */
        else if (fileName.equals("commercialFacility.csv")) {

            // 헤더 제거
            if (fields.length > 0
                    && fields[0].equals("순서")) {
                return;
            }

            if (fields.length < 10) {
                return;
            }

            String districtCode =
                    normalizeDistrictCode(fields[6]);

            if (districtCode.isEmpty()) {
                return;
            }

            String districtName =
                    fields[7].trim();


            /*
             * 상권 1개
             *
             * COMMERCIAL
             * |행정동명
             * |1
             */
            outputKey.set(districtCode);

            outputValue.set(
                    "COMMERCIAL|"
                    + districtName
                    + "|1"
            );

            context.write(
                    outputKey,
                    outputValue
            );
        }


        /*
         * =====================================================
         * 5. 주민등록인구
         * =====================================================
         *
         * population.csv
         *
         * 실제 CSV 컬럼
         *
         * index 0  행정구역
         * index 1  행정동코드
         * index 2  행정동
         * index 3  총인구수
         * index 4  연령구간인구수
         * index 5  0~9세
         * index 6  10~19세
         * index 7  20~29세
         * index 8  30~39세
         * index 9  40~49세
         * index 10 50~59세
         * index 11 60~69세
         * index 12 70~79세
         * index 13 80~89세
         * index 14 90~99세
         * index 15 100세 이상
         *
         * 이후 남자/여자 데이터가 이어짐.
         *
         * 이번 Job1에서는
         *
         * 총인구
         * 청년인구
         * 노인인구
         *
         * 만 사용한다.
         */
        else if (fileName.equals("population.csv")) {

            // 헤더 제거
            if (fields.length > 0
                    && fields[0].equals("행정구역")) {
                return;
            }

            if (fields.length < 16) {
                return;
            }


            /*
             * 서울특별시 전체 행 제외
             *
             * 서울특별시 전체 행은
             *
             * fields[1] = 1100000000
             * fields[2] = ""
             *
             * 이므로 행정동명이 비어 있다.
             */
            String districtName =
                    fields[2].trim();

            if (districtName.isEmpty()) {
                return;
            }


            /*
             * 행정동코드
             *
             * 주민등록인구는 10자리
             */
            String districtCode =
                    normalizeDistrictCode(fields[1]);

            if (districtCode.isEmpty()) {
                return;
            }


            /*
             * 총인구
             *
             * 예:
             *
             * "10,876"
             *
             * parseNumber()에서
             * comma를 제거한다.
             */
            double totalPopulation =
                    parseNumber(fields[3]);


            /*
             * =================================================
             * 청년인구
             * =================================================
             *
             * 20~29세
             * +
             * 30~39세
             */
            double youthPopulation =
                    parseNumber(fields[7])
                    + parseNumber(fields[8]);


            /*
             * =================================================
             * 노인인구
             * =================================================
             *
             * 60~69세
             * +
             * 70~79세
             * +
             * 80~89세
             * +
             * 90~99세
             * +
             * 100세 이상
             */
            double elderlyPopulation =
                    parseNumber(fields[11])
                    + parseNumber(fields[12])
                    + parseNumber(fields[13])
                    + parseNumber(fields[14])
                    + parseNumber(fields[15]);


            /*
             * POPULATION
             * |행정동명
             * |총인구
             * |청년인구
             * |노인인구
             */
            outputKey.set(districtCode);

            outputValue.set(
                    "POPULATION|"
                    + districtName
                    + "|"
                    + totalPopulation
                    + "|"
                    + youthPopulation
                    + "|"
                    + elderlyPopulation
            );

            context.write(
                    outputKey,
                    outputValue
            );
        }


        /*
         * =====================================================
         * 6. 생활인구
         * =====================================================
         *
         * livingPopulation.csv
         *
         * index 0  일자
         * index 1  시간
         * index 2  행정동코드
         * index 3  생활인구합계
         *
         * 청년
         *
         * index 7  남자 20~24세
         * index 8  남자 25~29세
         * index 9  남자 30~34세
         * index 10 남자 35~39세
         *
         * index 19 여자 20~24세
         * index 20 여자 25~29세
         * index 21 여자 30~34세
         * index 22 여자 35~39세
         *
         * 노인
         *
         * index 13 남자 60~64세
         * index 14 남자 65~69세
         * index 15 남자 70세 이상
         *
         * index 27 여자 60~64세
         * index 28 여자 65~69세
         * index 29 여자 70세 이상
         *
         * 생활인구 데이터는 같은 행정동에
         * 여러 날짜/시간의 레코드가 존재한다.
         *
         * Mapper에서는 각각의 레코드를
         * 그대로 Reducer로 전달한다.
         *
         * Reducer에서 전체 레코드 평균을 계산한다.
         */
        else if (fileName.equals("livingPopulation.csv")) {

            // 헤더 제거
            if (fields.length > 0
                    && fields[0].equals("일자")) {
                return;
            }

            if (fields.length < 30) {
                return;
            }


            /*
             * 생활인구 행정동코드
             *
             * 예:
             *
             * 11110515
             *
             * ↓
             *
             * 1111051500
             */
            String districtCode =
                    normalizeDistrictCode(fields[2]);

            if (districtCode.isEmpty()) {
                return;
            }


            /*
             * 전체 생활인구
             */
            double livingPopulation =
                    parseNumber(fields[3]);


            /*
             * =================================================
             * 청년 생활인구
             * =================================================
             *
             * 남자
             * 20~24
             * 25~29
             * 30~34
             * 35~39
             *
             * +
             *
             * 여자
             * 20~24
             * 25~29
             * 30~34
             * 35~39
             */
            double youthLivingPopulation =
                    parseNumber(fields[7])
                    + parseNumber(fields[8])
                    + parseNumber(fields[9])
                    + parseNumber(fields[10])
                    + parseNumber(fields[19])
                    + parseNumber(fields[20])
                    + parseNumber(fields[21])
                    + parseNumber(fields[22]);


            /*
             * =================================================
             * 노인 생활인구
             * =================================================
             *
             * 남자
             * 60~64
             * 65~69
             * 70세 이상
             *
             * +
             *
             * 여자
             * 60~64
             * 65~69
             * 70세 이상
             */
            double elderlyLivingPopulation =
                    parseNumber(fields[13])
                    + parseNumber(fields[14])
                    + parseNumber(fields[15])
                    + parseNumber(fields[27])
                    + parseNumber(fields[28])
                    + parseNumber(fields[29]);


            /*
             * 생활인구에는 별도의 행정동명이 없기 때문에
             * 행정동명은 전달하지 않는다.
             *
             * Reducer에서는 다른 데이터의 행정동명을
             * 사용한다.
             *
             * LIVING
             * |전체생활인구
             * |청년생활인구
             * |노인생활인구
             */
            outputKey.set(districtCode);

            outputValue.set(
                    "LIVING|"
                    + livingPopulation
                    + "|"
                    + youthLivingPopulation
                    + "|"
                    + elderlyLivingPopulation
            );

            context.write(
                    outputKey,
                    outputValue
            );
        }
    }


    /*
     * =========================================================
     * CSV Parser
     * =========================================================
     *
     * 단순 split(",") 대신 사용한다.
     *
     * 예:
     *
     * 1111051500,청운효자동,"10,876","1,006"
     *
     * 를
     *
     * 1111051500
     * 청운효자동
     * 10,876
     * 1,006
     *
     * 으로 정확하게 분리한다.
     *
     * 큰따옴표 안의 쉼표는
     * 컬럼 구분자로 취급하지 않는다.
     */
    private String[] parseCsvLine(String line) {

        java.util.List<String> fields =
                new java.util.ArrayList<String>();

        StringBuilder field =
                new StringBuilder();

        boolean insideQuotes = false;


        for (int i = 0; i < line.length(); i++) {

            char c = line.charAt(i);


            /*
             * 큰따옴표 처리
             */
            if (c == '"') {

                /*
                 * CSV에서 ""는
                 * 하나의 " 문자로 표현될 수 있다.
                 */
                if (insideQuotes
                        && i + 1 < line.length()
                        && line.charAt(i + 1) == '"') {

                    field.append('"');
                    i++;

                } else {

                    insideQuotes = !insideQuotes;
                }
            }


            /*
             * 따옴표 밖의 쉼표는
             * 컬럼 구분자
             */
            else if (c == ',' && !insideQuotes) {

                fields.add(
                        field.toString()
                );

                field.setLength(0);
            }


            /*
             * 일반 문자
             */
            else {

                field.append(c);
            }
        }


        /*
         * 마지막 컬럼 추가
         */
        fields.add(
                field.toString()
        );


        return fields.toArray(
                new String[fields.size()]
        );
    }


    /*
     * =========================================================
     * 숫자 변환
     * =========================================================
     *
     * 다음과 같은 데이터를 처리한다.
     *
     * 10,876
     * "10,876"
     * 13773.15
     * *
     * 빈 값
     *
     * 쉼표와 공백을 제거한다.
     *
     * "*"와 같은 비수치 데이터는 0으로 처리한다.
     */
    private double parseNumber(String value) {

        if (value == null) {
            return 0.0;
        }

        value =
                value.trim()
                        .replace(",", "");


        if (value.isEmpty()) {
            return 0.0;
        }


        /*
         * 생활인구에 '*'가 존재할 수 있다.
         *
         * 예:
         *
         * *
         */
        if (value.equals("*")) {
            return 0.0;
        }


        try {

            return Double.parseDouble(value);

        } catch (NumberFormatException e) {

            return 0.0;
        }
    }


    /*
     * =========================================================
     * 행정동코드 정규화
     * =========================================================
     *
     * 모든 데이터를 최종적으로 10자리로 통일한다.
     *
     * 유휴공간
     * 1168075000
     * ↓
     * 그대로
     *
     * 복지시설
     * 1129072500
     * ↓
     * 그대로
     *
     * 주민등록인구
     * 1111051500
     * ↓
     * 그대로
     *
     * 문화시설
     * 11140615
     * ↓
     * 1114061500
     *
     * 생활인구
     * 11110515
     * ↓
     * 1111051500
     *
     * 상권
     * 11140670
     * ↓
     * 1114067000
     */
    private String normalizeDistrictCode(String code) {

        if (code == null) {
            return "";
        }

        code = code.trim();


        if (code.isEmpty()) {
            return "";
        }


        /*
         * 8자리
         *
         * 뒤에 00을 붙여
         * 10자리로 만든다.
         */
        if (code.length() == 8) {

            return code + "00";
        }


        /*
         * 이미 10자리이면
         * 그대로 사용한다.
         */
        if (code.length() == 10) {

            return code;
        }


        /*
         * 그 외의 길이는
         * 잘못된 행정동코드로 판단
         */
        return "";
    }
}
