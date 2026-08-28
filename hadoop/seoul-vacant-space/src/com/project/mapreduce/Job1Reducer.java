package com.project.mapreduce;

import java.io.IOException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class Job1Reducer
        extends Reducer<Text, Text, Text, Text> {

    private final Text outputValue = new Text();


    @Override
    protected void reduce(
            Text key,
            Iterable<Text> values,
            Context context)
            throws IOException, InterruptedException {


        /*
         * =====================================================
         * 행정동코드
         * =====================================================
         *
         * Mapper에서 전달된 key
         *
         * 예:
         *
         * 1111051500
         */
        String districtCode =
                key.toString();


        /*
         * =====================================================
         * 시설 / 상권 개수
         * =====================================================
         */

        int vacantCount = 0;

        int cultureCount = 0;

        int welfareCount = 0;

        int commercialCount = 0;


        /*
         * =====================================================
         * 주민등록인구
         * =====================================================
         */

        double totalPopulation = 0.0;

        double youthPopulation = 0.0;

        double elderlyPopulation = 0.0;


        /*
         * =====================================================
         * 생활인구
         * =====================================================
         *
         * 같은 행정동에 여러 날짜 / 시간 데이터가 존재한다.
         *
         * 따라서 합계를 구한 뒤
         * 마지막에 평균을 계산한다.
         */

        double livingPopulationSum = 0.0;

        double youthLivingPopulationSum = 0.0;

        double elderlyLivingPopulationSum = 0.0;

        int livingPopulationCount = 0;


        /*
         * =====================================================
         * 유휴공간 상세정보
         * =====================================================
         *
         * 현재 Job1에서는 유휴공간의 개수뿐만 아니라
         * 실제 추천 대상이 될 시설 정보를 유지한다.
         *
         * 예:
         *
         * VACANT
         * |시설명
         * |주소
         * |면적
         * |위도
         * |경도
         * |행정동명
         *
         * 다만 현재 Job1의 최종 CSV에는
         * 유휴공간 상세정보를 출력하지 않는다.
         *
         * 따라서 여기서는 개수만 계산한다.
         */


        /*
         * =====================================================
         * Mapper 결과 처리
         * =====================================================
         */

        for (Text value : values) {

            String data =
                    value.toString();


            /*
             * 혹시 빈 데이터가 들어오면 무시
             */
            if (data == null
                    || data.trim().isEmpty()) {

                continue;
            }


            /*
             * "|" 기준으로 데이터 분리
             */
            String[] fields =
                    data.split("\\|", -1);


            if (fields.length == 0) {
                continue;
            }


            /*
             * =================================================
             * 1. 유휴공간
             * =================================================
             *
             * Mapper:
             *
             * VACANT
             * |시설명
             * |주소
             * |면적
             * |위도
             * |경도
             * |행정동명
             */
            if (fields[0].equals("VACANT")) {

                vacantCount++;
            }


            /*
             * =================================================
             * 2. 문화시설
             * =================================================
             *
             * Mapper:
             *
             * CULTURE
             * |행정동명
             * |1
             */
            else if (fields[0].equals("CULTURE")) {

                /*
                 * 문화시설 1개
                 */
                if (fields.length >= 3) {

                    cultureCount +=
                            parseInt(fields[2]);

                } else {

                    /*
                     * 혹시 Mapper에서
                     * CULTURE|1 형태가 들어와도
                     * 안전하게 처리
                     */
                    cultureCount++;
                }
            }


            /*
             * =================================================
             * 3. 복지시설
             * =================================================
             *
             * Mapper:
             *
             * WELFARE
             * |행정동명
             * |1
             */
            else if (fields[0].equals("WELFARE")) {

                /*
                 * 복지시설 1개
                 */
                if (fields.length >= 3) {

                    welfareCount +=
                            parseInt(fields[2]);

                } else {

                    welfareCount++;
                }
            }


            /*
             * =================================================
             * 4. 상권
             * =================================================
             *
             * Mapper:
             *
             * COMMERCIAL
             * |행정동명
             * |1
             */
            else if (fields[0].equals("COMMERCIAL")) {

                /*
                 * 상권 1개
                 */
                if (fields.length >= 3) {

                    commercialCount +=
                            parseInt(fields[2]);

                } else {

                    commercialCount++;
                }
            }


            /*
             * =================================================
             * 5. 주민등록인구
             * =================================================
             *
             * Mapper:
             *
             * POPULATION
             * |행정동명
             * |총인구
             * |청년인구
             * |노인인구
             *
             * 예:
             *
             * POPULATION
             * |청운효자동
             * |10876
             * |2775
             * |3024
             */
            else if (fields[0].equals("POPULATION")) {

                if (fields.length < 5) {
                    continue;
                }


                /*
                 * 행정동명은 사용하지 않는다.
                 *
                 * fields[1]
                 *
                 * ↓
                 *
                 * 무시
                 */


                /*
                 * 총인구
                 */
                totalPopulation =
                        parseDouble(fields[2]);


                /*
                 * 청년인구
                 */
                youthPopulation =
                        parseDouble(fields[3]);


                /*
                 * 노인인구
                 */
                elderlyPopulation =
                        parseDouble(fields[4]);
            }


            /*
             * =================================================
             * 6. 생활인구
             * =================================================
             *
             * Mapper:
             *
             * LIVING
             * |전체생활인구
             * |청년생활인구
             * |노인생활인구
             *
             * 예:
             *
             * LIVING
             * |16968.16
             * |4702.32
             * |3930.62
             *
             * 같은 행정동에 여러 개가 존재할 수 있으므로
             * 모두 더한 뒤 평균을 계산한다.
             */
            else if (fields[0].equals("LIVING")) {

                if (fields.length < 4) {
                    continue;
                }


                /*
                 * 전체 생활인구
                 */
                double livingPopulation =
                        parseDouble(fields[1]);


                /*
                 * 청년 생활인구
                 */
                double youthLivingPopulation =
                        parseDouble(fields[2]);


                /*
                 * 노인 생활인구
                 */
                double elderlyLivingPopulation =
                        parseDouble(fields[3]);


                /*
                 * 합계
                 */
                livingPopulationSum +=
                        livingPopulation;


                youthLivingPopulationSum +=
                        youthLivingPopulation;


                elderlyLivingPopulationSum +=
                        elderlyLivingPopulation;


                /*
                 * 생활인구 레코드 개수
                 */
                livingPopulationCount++;
            }
        }


        /*
         * =====================================================
         * 생활인구 평균 계산
         * =====================================================
         */

        double averageLivingPopulation = 0.0;

        double averageYouthLivingPopulation = 0.0;

        double averageElderlyLivingPopulation = 0.0;


        if (livingPopulationCount > 0) {

            averageLivingPopulation =
                    livingPopulationSum
                    / livingPopulationCount;


            averageYouthLivingPopulation =
                    youthLivingPopulationSum
                    / livingPopulationCount;


            averageElderlyLivingPopulation =
                    elderlyLivingPopulationSum
                    / livingPopulationCount;
        }


        /*
         * =====================================================
         * 최종 출력
         * =====================================================
         *
         * 행정동명은 출력하지 않는다.
         *
         * 최종 구조:
         *
         * 행정동코드
         *
         * +
         *
         * 유휴공간수
         * 문화시설수
         * 복지시설수
         * 상권수
         * 총인구
         * 청년인구
         * 노인인구
         * 평균생활인구
         * 평균청년생활인구
         * 평균노인생활인구
         *
         *
         * 최종적으로:
         *
         * 1111051500
         *     0
         *     11
         *     0
         *     5
         *     10876.0
         *     2775.0
         *     3024.0
         *     16968.16...
         *     4702.32...
         *     3930.62...
         */


        outputValue.set(
                vacantCount
                + ","
                + cultureCount
                + ","
                + welfareCount
                + ","
                + commercialCount
                + ","
                + totalPopulation
                + ","
                + youthPopulation
                + ","
                + elderlyPopulation
                + ","
                + averageLivingPopulation
                + ","
                + averageYouthLivingPopulation
                + ","
                + averageElderlyLivingPopulation
        );


        /*
         * Hadoop 기본 출력:
         *
         * key + TAB + value
         *
         * 따라서 실제 part-r-00000:
         *
         * 1111051500
         *     [TAB]
         * 0,11,0,5,10876.0,...
         */
        context.write(
                key,
                outputValue
        );
    }


    /*
     * =========================================================
     * 정수 변환
     * =========================================================
     */

    private int parseInt(String value) {

        if (value == null) {
            return 0;
        }


        value =
                value.trim();


        if (value.isEmpty()) {
            return 0;
        }


        try {

            return Integer.parseInt(value);

        } catch (NumberFormatException e) {

            return 0;
        }
    }


    /*
     * =========================================================
     * 실수 변환
     * =========================================================
     */

    private double parseDouble(String value) {

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
         * 생활인구 등에
         * "*"가 들어올 수 있는 경우
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
}
