package indexer.dataunit;

import indexer.Indexing;

import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Map;

public class Statistics {
    public int CALL;
    public int EXTERNAL_CALL;
    public int INTERNAL_CALL;
    public int EXCEPTION_MULTI_DEFS;
    public int EXCEPTION_NULL_BINGDING;
    private long startTimeTotal;
    private long endTimeTotal;
    private long startTimeCollection;
    private long endTimeCollection;
    private long startTimeIndexing;
    private long endTimeIndexing;

    public Statistics() {
        this.CALL = 0;
        this.EXTERNAL_CALL = 0;
        this.INTERNAL_CALL = 0;
        this.EXCEPTION_MULTI_DEFS = 0;
        this.EXCEPTION_NULL_BINGDING = 0;
    }

    public void setStartTimeTotal(long startTimeTotal) {
        this.startTimeTotal = startTimeTotal;
    }

    public void setEndTimeTotal(long endTimeTotal) {
        this.endTimeTotal = endTimeTotal;
    }

    public void setStartTimeCollection(long startTimeCollection) {
        this.startTimeCollection = startTimeCollection;
    }

    public void setEndTimeCollection(long endTimeCollection) {
        this.endTimeCollection = endTimeCollection;
    }

    public void setStartTimeIndexing(long startTimeIndexing) {
        this.startTimeIndexing = startTimeIndexing;
    }

    public void setEndTimeIndexing(long endTimeIndexing) {
        this.endTimeIndexing = endTimeIndexing;
    }

    public double getTimeCostTotal() {
        if (endTimeTotal == startTimeTotal)
            return -1.0;
        else
            return (endTimeTotal - startTimeTotal) / 1000.0;
    }

    public double getTimeCostCollection() {
        if (endTimeCollection == startTimeCollection)
            return -1.0;
        else
            return (endTimeCollection - startTimeCollection) / 1000.0;
    }

    public double getTimeCostIndexing() {
        if (endTimeIndexing == startTimeIndexing)
            return -1.0;
        else
            return (endTimeIndexing - startTimeIndexing) / 1000.0;
    }

    @Override
    public String toString() {
        return "\n统计信息{" +
                "\n代码总行数: " + computeLineNumber() +
                "\n类数量：" + Indexing.project.projectData.size() +
                "\n函数调用数量: " + CALL +
                "\n检测到内部函数调用: " + INTERNAL_CALL +
                "\n检测到外部函数调用: " + EXTERNAL_CALL +
                "\n调用无法解析异常(NULL Bindings): " + EXCEPTION_NULL_BINGDING +
                "\n多定义异常：" + EXCEPTION_MULTI_DEFS +
                "\n总耗时: " + getTimeCostTotal() + "s" +
                ", 收集数据耗时: " + getTimeCostCollection() + "s" +
                ", 索引耗时: " + getTimeCostIndexing() + "s" +
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
