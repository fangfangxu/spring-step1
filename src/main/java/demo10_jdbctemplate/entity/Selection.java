package demo10_jdbctemplate.entity;

import java.util.Date;

public class Selection {
    private int sid;
    private int cid;
    private Date selTime;
    private int score;

    public Selection() {

    }

    @Override
    public String toString() {
        return "Selection{" +
                "sid=" + sid +
                ", cid=" + cid +
                ", selTime=" + selTime +
                ", score=" + score +
                '}';
    }

    public Selection(int sid, int cid, Date selTime, int score) {
        this.sid = sid;
        this.cid = cid;
        this.selTime = selTime;
        this.score = score;
    }

    public int getSid() {
        return sid;
    }

    public void setSid(int sid) {
        this.sid = sid;
    }

    public int getCid() {
        return cid;
    }

    public void setCid(int cid) {
        this.cid = cid;
    }

    public Date getSelTime() {
        return selTime;
    }

    public void setSelTime(Date selTime) {
        this.selTime = selTime;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}
