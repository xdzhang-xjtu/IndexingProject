package indexer.dataunit;

import indexer.Indexing;
import indexer.dataunit.node.ClassNode;

import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;

public class Statistics {
    public int CALL;
    public int EXTERNAL_CALL;
    public int INTERNAL_CALL;
    public int EXCEPTION_MULTI_DEFS;
    public int EXCEPTION_NULL_BINGDING;
    private long startTime;
    private long endTime;

    public Statistics() {
        this.CALL = 0;
        this.EXTERNAL_CALL = 0;
        this.INTERNAL_CALL = 0;
        this.EXCEPTION_MULTI_DEFS = 0;
        this.EXCEPTION_NULL_BINGDING = 0;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public double getTimeCost() {
        if (endTime == startTime)
            return -1.0;
        else
            return (double) (endTime - startTime) / 1000.0;
    }


    @Override
    public String toString() {
        return "\n统计信息{" +
                " 代码总行数: " + computeLineNumber() +
                ", 类数量：" + Indexing.project.projectData.size() +
                ", 函数调用数量: " + CALL +
                ", 检测到外部函数调用: " + EXTERNAL_CALL +
                ", 检测到内部函数调用: " + INTERNAL_CALL +
                ", 调用无法解析异常(NULL Bindings): " + EXCEPTION_NULL_BINGDING +
                ", 多定义异常：" + EXCEPTION_MULTI_DEFS +
                ", 耗时: " + getTimeCost() + "s" +
                '}';
    }

    public int computeLineNumber() {
        int LINENUMBER = 0;
        FileReader fr = null;
        BufferedReader br = null;
        for (Map.Entry<String, ClassNode> classEntry : Indexing.project.projectData.entrySet()) {
            try {
                File file = new File(classEntry.getKey());
                if (!file.exists() || !classEntry.getKey().endsWith(".java")) {
                    System.err.println("ERROR: File is not qualified!");
                    System.exit(0);
                }
                int lineNumber = 0;
                fr = new FileReader(file);
                br = new BufferedReader(fr);
                String str = "";
                while ((str = br.readLine()) != null) {
                    if (isBlankLine(str))
                        lineNumber++;
                }
                LINENUMBER = LINENUMBER + lineNumber;
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (br != null) {
                    try {
                        br.close();
                    } catch (Exception e) {
                    }
                }
                if (fr != null) {
                    try {
                        fr.close();
                    } catch (Exception e) {
                    }
                }
            }
        }
        return LINENUMBER;
    }

    //是否是空行
    public boolean isBlankLine(String i) {
        if (i.trim().length() == 0) {
            return false;
        } else {
            return true;
        }
    }

}
