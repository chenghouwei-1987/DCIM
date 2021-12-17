package com.cmiot.gateway.utils;

import com.cmiot.gateway.config.IGatewayConfig;
import com.cmiot.gateway.extensions.logging.ILogger;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.util.Base64;

import static com.cmiot.gateway.extensions.logging.LoggerFormat.Action.RUNTIME;

/**
 * SasToken生成器
 */
@Slf4j
public class SasTokenGenerator {

    private static ILogger logger = ExtensionUtils.getLogger();

    private static IGatewayConfig gatewayConfig = ExtensionUtils.getGatewayConfig();

    private static String gatewayToken = null;

    private static final String MAC_NAME = "HmacSHA1";

    private static final String ENCODING = "UTF-8";

    private static final String EXP = "1577721600";

    private static final String GATEWAY_VERSION = "v1.0.0";

    private static final String DEVICE_VERSION = "2018-10-31";

    private static final String PRODUCT = "products";

    private static final String DEVICE = "devices";

    private static final String GATEWAY = "gw";

    private static final String INSTANCE = "instance";

    private SasTokenGenerator() {
    }

    /**
     * 生成设备SasToken
     *
     * @param productId  产品ID
     * @param deviceName 设备名称
     * @param deviceKey  设备Key
     * @return 设备SasToken
     */
    public static String deviceSasToken(long productId, String deviceName, String deviceKey) {
        return genSasToken(PRODUCT, String.valueOf(productId), DEVICE, deviceName, deviceKey, DEVICE_VERSION);
    }

    /**
     * 生成网关相关SasToken
     *
     * @return 网关实例SasToken
     */
    public static String gatewaySasToken() {
        if (gatewayToken == null) {
            String serviceId = gatewayConfig.getServiceId();
            String instanceName = gatewayConfig.getInstanceName();
            String instanceKey = gatewayConfig.getInstanceKey();
            if (StringUtils.isEmpty(serviceId)
                    || StringUtils.isEmpty(instanceName)
                    || StringUtils.isEmpty(instanceKey)) {
                logger.logInnerError(log, RUNTIME, serviceId, instanceName, "illegal gateway config with empty value", null);
                System.exit(1);
            }
            gatewayToken = genSasToken(GATEWAY, serviceId, INSTANCE, instanceName, instanceKey, GATEWAY_VERSION);
        }
        return gatewayToken;
    }

    /**
     * 生成SasToken
     *
     * @param serviceType  服务类型  gw或product
     * @param serviceId    服务ID
     * @param instanceType 实例类型  instance或devices
     * @param instanceName 实例名称
     * @param instanceKey  实例Key
     * @param version      版本
     * @return SasToken
     */
    private static String genSasToken(String serviceType, String serviceId, String instanceType, String instanceName, String instanceKey, String version) {
        try {
            String res = serviceType
                    + "/"
                    + serviceId
                    + "/"
                    + instanceType
                    + "/"
                    + instanceName;
            String encryptTxt = EXP +
                    "\n" +
                    "sha1" +
                    "\n" +
                    res +
                    "\n" +
                    version;
            String sign = HmacSHA1Encrypt(encryptTxt, instanceKey);
            String urlSign = URLEncoder.encode(sign, "UTF-8");
            return "et=" +
                    EXP +
                    "&method=sha1&res=" +
                    res +
                    "&version=" +
                    version +
                    "&sign=" +
                    urlSign;
        } catch (Exception e) {
            logger.logInnerError(log, RUNTIME, "generate sasToken failed", e);
            return null;
        }
    }

    /**
     * SHA1编码
     *
     * @param encryptText 编码文本
     * @param encryptKey  编码Key
     * @return SHA1编码
     * @throws Exception Exception
     */
    private static String HmacSHA1Encrypt(String encryptText, String encryptKey) throws Exception {
        byte[] key = Base64.getDecoder().decode(encryptKey);
        //根据给定的字节数组构造一个密钥,第二参数指定一个密钥算法的名称
        SecretKey secretKey = new SecretKeySpec(key, MAC_NAME);
        //生成一个指定 Mac 算法 的 Mac 对象
        Mac mac = Mac.getInstance(MAC_NAME);
        //用给定密钥初始化 Mac 对象
        mac.init(secretKey);
        byte[] text = encryptText.getBytes(ENCODING);
        //完成 Mac 操作
        return Base64.getEncoder().encodeToString(mac.doFinal(text));
    }
}
