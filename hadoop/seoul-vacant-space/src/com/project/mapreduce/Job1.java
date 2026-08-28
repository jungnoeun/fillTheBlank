package com.project.mapreduce;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class Job1 {

    public static void main(String[] args)
            throws Exception {

        /*
         * 실행 시 인자
         *
         * args[0] = HDFS 입력 경로
         * args[1] = HDFS 출력 경로
         *
         * 예:
         * /project/input
         * /project/output/job1
         */

        if (args.length != 2) {

            System.err.println(
                "Usage: Job1 <input path> <output path>"
            );

            System.exit(2);
        }


        /*
         * Hadoop Configuration
         */
        Configuration conf =
                new Configuration();


        /*
         * Hadoop Job 생성
         */
        Job job =
                Job.getInstance(
                    conf,
                    "Seoul Vacant Space Job1"
                );


        /*
         * 실행할 Jar의 기준 클래스
         */
        job.setJarByClass(Job1.class);


        /*
         * Mapper 설정
         */
        job.setMapperClass(
                Job1Mapper.class
        );


        /*
         * Reducer 설정
         */
        job.setReducerClass(
                Job1Reducer.class
        );


        /*
         * Mapper 출력 타입
         *
         * Mapper:
         *
         * Text 행정동코드
         * Text 데이터
         */
        job.setMapOutputKeyClass(
                Text.class
        );

        job.setMapOutputValueClass(
                Text.class
        );


        /*
         * Reducer 최종 출력 타입
         *
         * Reducer:
         *
         * Text 행정동코드
         * Text CSV 데이터
         */
        job.setOutputKeyClass(
                Text.class
        );

        job.setOutputValueClass(
                Text.class
        );


        /*
         * HDFS 입력 경로
         */
        FileInputFormat.addInputPath(
                job,
                new Path(args[0])
        );


        /*
         * HDFS 출력 경로
         */
        FileOutputFormat.setOutputPath(
                job,
                new Path(args[1])
        );


        /*
         * Job 실행
         *
         * true:
         * 실행 진행 상황을 콘솔에 표시
         */
        System.exit(
                job.waitForCompletion(true)
                ? 0
                : 1
        );
    }
}
