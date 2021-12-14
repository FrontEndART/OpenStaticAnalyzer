import java.util.Date;
class CloneInvocationTest {
    private Date date;
    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
    public Date getDateE() {
        return (Date) date.clone();
    }

    public void setDateE(Date date) {
        this.date = (Date) date.clone();
    }
	
	public Date foo() {
		return new Date(date.getTime());
	}
}