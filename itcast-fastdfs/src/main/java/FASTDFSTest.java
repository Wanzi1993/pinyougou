import org.csource.fastdfs.*;
import org.junit.Test;

/**
 * Date:2018-09-03
 * Author:Wanzi
 * Desc:
 */
public class FASTDFSTest {
    @Test
    public void test() throws Exception{
        //配置文件路径
        String conf_filename = ClassLoader.getSystemResource("fastdfs/tracker.conf").getPath();
        //设置初始化参数
        ClientGlobal.init(conf_filename);

        //1.创建TrackerServer服务器
        TrackerClient trackerClient = new TrackerClient();
        TrackerServer trackerServer = trackerClient.getConnection();

        //2.创建StorageClient对象

        StorageServer storageServer = null;
        //创建存储客户端StorageClient
        StorageClient storageClient = new StorageClient(trackerServer, storageServer);

        //上传图片
        String[] uploadFile = storageClient.upload_appender_file("D:\\丸子\\Pictures\\Saved Pictures\\123456.png", "png", null);

        if (uploadFile != null && uploadFile.length > 0){
            for (String s : uploadFile) {
                System.out.println(s);
            }
        }

        //获取存储服务器信息
        ServerInfo[] serverInfos = trackerClient.getFetchStorages(trackerServer, uploadFile[0], uploadFile[1]);

        for (ServerInfo serverInfo : serverInfos) {
            System.out.println("服务器存储的ip:"+serverInfo.getIpAddr() + ";port是:" + serverInfo.getPort());
        }
        String url = "http://"+serverInfos[0].getIpAddr()  + "/" + uploadFile[0] + "/" + uploadFile[1];

        System.out.println("url:" + url);
    }


}
