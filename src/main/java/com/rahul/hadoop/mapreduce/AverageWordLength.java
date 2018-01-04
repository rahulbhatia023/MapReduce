package com.rahul.hadoop.mapreduce;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;

public class AverageWordLength {
    public static class AverageWordLengthMapper extends Mapper<LongWritable, Text, Text, IntWritable> {
        public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            String[] words = value.toString().split(",");
            for (String word : words) {
                if (word.trim().length() > 0) {
                    context.write(new Text(String.valueOf(word.trim().charAt(0))), new IntWritable(word.length()));
                }
            }
        }
    }

    public static class AverageWordLengthReducer extends Reducer<Text, IntWritable, Text, IntWritable> {
        public void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
            int sum = 0, count = 0;
            for (IntWritable value : values) {
                sum += value.get();
                count += 1;
            }
            int average = sum / count;
            context.write(key, new IntWritable(average));
        }
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
        Job job = new Job(new Configuration(), "average_word_length");

        job.setJarByClass(AverageWordLength.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);
        job.setMapperClass(AverageWordLength.AverageWordLengthMapper.class);
        job.setReducerClass(AverageWordLength.AverageWordLengthReducer.class);
        FileInputFormat.addInputPath(job, new Path(ClassLoader.getSystemResource("words_input.txt").getPath()));
        FileOutputFormat.setOutputPath(job, new Path("output"));

        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}
