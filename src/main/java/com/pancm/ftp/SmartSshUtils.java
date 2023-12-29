//package com.pancm.ftp;
//
//import lombok.extern.slf4j.Slf4j;
//import net.schmizz.sshj.SSHClient;
//import net.schmizz.sshj.sftp.SFTPClient;
//import net.schmizz.sshj.transport.verification.PromiscuousVerifier;
//
//import java.io.IOException;
//
//
//@Slf4j
//public final class SmartSshUtils {
//
//	public static boolean testSFTP(String hostName,
//								   String username,
//								   String password){
//		SSHClient ssh = new SSHClient();
//		SFTPClient sftpClient = null;
//		try {
//			//ssh.loadKnownHosts(); to skip host verification
//			ssh.addHostKeyVerifier(new PromiscuousVerifier());
//			ssh.connect(hostName);
//			ssh.authPassword(username, password);
//			sftpClient = ssh.newSFTPClient();
//			return true;
//		}catch (IOException e) {
//			e.printStackTrace();
//		}
//
//		return false;
//	}
//
//
//	public static void downLoadFileBySsh(String hostName,
//								String username,
//								String password,
//								String srcFilePath,
//								String targetFilePath
//	) {
//		SSHClient ssh = new SSHClient();
//		SFTPClient sftpClient = null;
//		try {
//			//ssh.loadKnownHosts(); to skip host verification
//			ssh.addHostKeyVerifier(new PromiscuousVerifier());
//			ssh.connect(hostName);
//			ssh.authPassword(username, password);
//			sftpClient = ssh.newSFTPClient();
//			sftpClient.get(srcFilePath, targetFilePath);
//			//create a folder
////			sftpClient.mkdir("/opt/app/testFolder");
//			//sftpClient.mkdirs("");创建多级文件夹
//			//sftpClient.rmdir("");重命名文件夹
//			//sftpClient.ls(""); //列出当前目录
//		} catch (IOException e) {
//			log.error(e.getMessage(), e);
//		} finally {
//			if (null != sftpClient) {
//				try {
//					sftpClient.close();
//				} catch (IOException e) {
//					log.error(e.getMessage(), e);
//				}
//			}
//			try {
//				ssh.disconnect();
//			} catch (IOException e) {
//				log.error(e.getMessage(), e);
//			}
//		}
//	}
//
//	/**
//	 * 静态工具类应该禁用构造方法
//	 */
//	private SmartSshUtils(){}
//
//
//	public static void main(String[] args) {
//		String hostName="192.168.9.80";
//		String username="root";
//		String password="Admin#12$34!";
//		String srcFilePath="/home/release/file";
//		String targetFilePath="D:\\d1";
//
//		SmartSshUtils.downLoadFileBySsh(hostName,username,password,srcFilePath,targetFilePath);
//
//	}
//}
