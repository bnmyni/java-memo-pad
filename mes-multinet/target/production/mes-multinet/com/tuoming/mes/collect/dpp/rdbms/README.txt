目录功能说明：
/impl：具体数据库操作的实现
/DataAdapter：数据适配器接口，通过该接口来实现对不同类型数据（数据库、文件等）的访问
/DataAdapterPool：自己实现的数据连接池
/DbOperation：数据库操作工具类，封装了数据库操作常用方法
/DbStat：数据库连接状态定义
/DbType：数据库类型定义
/ExecuteResult：用来封装查询结果的类
/TableSynchronizer： 定义数据表同步器接口