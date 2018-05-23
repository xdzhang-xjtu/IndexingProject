package indexer.dataunit;

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
                "函数调用总数：" + CALL +
                ", 外部函数调用：" + EXTERNAL_CALL +
                ", 内部函数调用：" + INTERNAL_CALL +
                ", 多定义异常：" + EXCEPTION_MULTI_DEFS +
                ", 调用无法解析异常：" + EXCEPTION_NULL_BINGDING +
                ", 耗时：" + getTimeCost() + "s" +
                '}';
    }

}
