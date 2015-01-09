package framework.data;

public enum DBDriver {
    MS_SQL("com.microsoft.sqlserver.jdbc.SQLServerDriver"),
    SYBASE("com.sybase.jdbc3.jdbc.SybDriver");

    final String classPath;

    DBDriver(String classPath) {
        this.classPath = classPath;
    }

    public String getClassPath() {
        return classPath;
    }
}
