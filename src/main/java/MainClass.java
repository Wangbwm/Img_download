
import java.util.concurrent.*;

import static java.lang.Thread.sleep;

public class MainClass {
    public static ConcurrentLinkedQueue<ProxyIp>proxyIps = new ConcurrentLinkedQueue<>();
    public static ConcurrentLinkedQueue<String>ImgName = new ConcurrentLinkedQueue<>();
    public static int number=1;
    public static void main(String[] args) throws InterruptedException {
/*
        ExecutorService IpPool = new ThreadPoolExecutor(20,20,0, TimeUnit.SECONDS, new ArrayBlockingQueue<>(512));
            for(int i=1;i<10;++i){
                String URL="https://free.kuaidaili.com/free/inha/" + i;
                //String URL="http://www.ip3366.net/?stype=1&page="+i;
                GetIp GetIPThread=new GetIp(proxyIps,URL);
                GetIPThread.setProxyIps(proxyIps);
                GetIPThread.setURL(URL);
                IpPool.execute(GetIPThread);
                sleep(5000);
            }

        long beginTime = System.currentTimeMillis();//开始时间
        long overTime = 180 * 1000;//运行时间
            while(true){
                long nowTime = System.currentTimeMillis();
                if((nowTime - beginTime) > overTime) {
                    IpPool.shutdownNow();
                    break;
                }
                IpPool.shutdown();//杀死执行完成的线程
                if(IpPool.isTerminated()) {//判断线程池是否空了
                    System.out.println("IP代理获取完成");
                    break;
                }


            }

*/
        GetImg GetImgThread=new GetImg(proxyIps,ImgName);
        GetImgThread.run();
        while(true){
            if(GetImgThread.end()){
                System.out.println("获取IMG信息成功");
                break;
            }
        }
        ExecutorService pool = new ThreadPoolExecutor(20,20,0, TimeUnit.SECONDS, new ArrayBlockingQueue<>(512), // 使用有界队列，避免OOM
                new ThreadPoolExecutor.DiscardPolicy());
        while(!ImgName.isEmpty())
        {
            pool.execute(new Download(ImgName,proxyIps));
            sleep(1000);
        }
        while(true) {
            pool.shutdown();//杀死执行完成的线程
            if(pool.isTerminated()) {//判断线程池是否空了
                System.out.println("图片下载完成");
                break;
            }
        }

    }

}