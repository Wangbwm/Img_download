import org.apache.http.HttpHost;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;


class ProxyIp {

    private int id;
    private String ip;

    private Integer port;

    public void setIp(String ip) {
        this.ip = ip;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIp() {
        return ip;
    }

    public Integer getPort() {
        return port;
    }

    boolean test(String ip, Integer port) {
        try {
            //创建httpClient实例
            CloseableHttpClient httpClient = HttpClients.createDefault();
            //创建httpGet实例
            HttpGet httpGet = new HttpGet("https://www.baidu.com/");
            //设置代理IP，设置连接超时时间 、 设置 请求读取数据的超时时间 、 设置从connect Manager获取Connection超时时间、
            HttpHost proxy = new HttpHost(ip, port);
            RequestConfig requestConfig = RequestConfig.custom()
                    .setProxy(proxy)
                    .setConnectTimeout(2000)
                    .setSocketTimeout(2000)
                    .setConnectionRequestTimeout(2000)
                    .setCookieSpec(CookieSpecs.STANDARD)
                    .build();
            httpGet.setConfig(requestConfig);
            //设置请求头消息
            httpGet.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like " +
                    "Gecko) Chrome/62.0.3202.94 Safari/537.36");
            httpGet.addHeader("Cookie","PSTM=1641900285; BDUSS=E0zYW14dmNXfnk2eXJlamNaMEhOS0IzVmFTREJTZUx3a2kxaUx4cTFveGhrUnhpRVFBQUFBJCQAAAAAAAAAAAEAAADgs0g7d2FuZ2J3MDIAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAGEE9WFhBPVhM; BDUSS_BFESS=E0zYW14dmNXfnk2eXJlamNaMEhOS0IzVmFTREJTZUx3a2kxaUx4cTFveGhrUnhpRVFBQUFBJCQAAAAAAAAAAAEAAADgs0g7d2FuZ2J3MDIAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAGEE9WFhBPVhM; MCITY=-:; BAIDUID=B92154936867037274FDB58AC29C8DB5:FG=1; BIDUPSID=A96DDF23AD19CECDF32CC0DFCDD40E1B; BAIDUID_BFESS=B92154936867037274FDB58AC29C8DB5:FG=1; H_PS_PSSID=36542_36461_36255_36726_36454_36414_36668_36453_36690_36165_36776_36775_36746_36760_36771_36765_26350_36681; delPer=0; PSINO=1; BA_HECTOR=8021812lag01ah2la481mgj31hdab5c17; ZFY=gY6e:AiEXHzCIY0l8aoTyEXN664tKx2GTwiUjgIk2pnA:C; RT=\"z=1&dm=baidu.com&si=49x40wa08c1&ss=l5qknk88&sl=2&tt=216&bcn=https://fclog.baidu.com/log/weirwood?type=perf&ld=2d1&ul=h8x&hd=ha0\"; H_WISE_SIDS=107319_110085_131861_180636_188749_194530_196428_197711_199566_204905_206125_208721_208809_209202_209568_210291_210296_210322_212295_212797_212874_213044_213080_213158_213307_213359_214109_214129_214137_214143_214189_214791_215127_215730_216040_216517_216616_216741_216849_216883_216942_217087_217186_218024_218329_218445_218454_218537_218548_218566_218593_218597_218664_218833_218853_219064_219249_219360_219363_219413_219448_219451_219548_219667_219713_219733_219738_219741_219814_219820_219842_219943_219946_220068_220071_220090_220221_220339_220394_220556_220800_221106_221107_221116_221119_221120_221380_221381_221457; rsv_i=865cLbzPBwPS+NfFMZMeQKQrxr34GqxOQ3mGMIAGapmN81uG2P6Y6WX7LmqQ4a7ulbujGTx5vfhSaEtE6/bStCVmXHfzr1A; H_WISE_SIDS_BFESS=107319_110085_131861_180636_188749_194530_196428_197711_199566_204905_206125_208721_208809_209202_209568_210291_210296_210322_212295_212797_212874_213044_213080_213158_213307_213359_214109_214129_214137_214143_214189_214791_215127_215730_216040_216517_216616_216741_216849_216883_216942_217087_217186_218024_218329_218445_218454_218537_218548_218566_218593_218597_218664_218833_218853_219064_219249_219360_219363_219413_219448_219451_219548_219667_219713_219733_219738_219741_219814_219820_219842_219943_219946_220068_220071_220090_220221_220339_220394_220556_220800_221106_221107_221116_221119_221120_221380_221381_221457; BDSVRBFE=Go; BDSVRTM=76; __bsi=11266667421198517276_00_64_N_R_0_0303_c02f_Y; plus_lsv=e9e1d7eaf5c62da9; plus_cv=1::m:7.94e+147; SE_LAUNCH=5:27635841");
            CloseableHttpResponse response = httpClient.execute(httpGet);

            return response.getStatusLine().getStatusCode() == HttpStatus.SC_OK;
        } catch (Exception e) {
            //log.info(e.getMessage());
            System.out.println(e.getMessage());
        }
        return false;
        //log.warn("ip:{}不可用", ip);

    }
}