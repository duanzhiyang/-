package com.ming.nio;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class Server implements Runnable{
	
	private Selector selector;
	private ByteBuffer buffer = ByteBuffer.allocate(1024);
	
	public Server(int port){
		try {
			//��һ��ѡ����
			selector = Selector.open();
			//�򿪷������׽���ͨ����
			ServerSocketChannel ssc = ServerSocketChannel.open();
			//���÷�����Ϊ��������ʽ  ������ͨ��������ģʽ��
			ssc.configureBlocking(false);
			//�� ServerSocket �󶨵��ض���ַ��IP ��ַ�Ͷ˿ںţ���
			ssc.bind(new InetSocketAddress(port));
			/*
			 * �ѷ�����ͨ��ע�ᵽ��·����ѡ�����ϣ�����������״̬
			 * 
			 * �������ѡ����ע���ͨ��������һ��ѡ�����
			 *	�˷���������֤��ͨ���Ƿ��Ѵ򿪣��Լ������ĳ�ʼ��ز������Ƿ���Ч��
			 *	������������ѡ����ע���˴�ͨ�������ڽ�����ز���������Ϊ����ֵ�󣬷��ر�ʾ��ע���ѡ�����
			 */
			ssc.register(selector, SelectionKey.OP_ACCEPT);
			System.out.println("server start whit port:"+port);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		while(true){
			try {
				//�������ﴦ���¼���Ҳ�������ģ��¼������ͻ������ӣ��ͻ��˷������ݵ���,�Լ��ͻ��˶Ͽ����ӵȵ�
                //��û���¼�������Ҳ������  
				//ѡ��һ���������Ӧ��ͨ����Ϊ I/O ����׼��������
				System.out.println("��������1");
				selector.select();
				System.out.println("��������2");
				//���������Ѿ�ע�ᵽ��·����ѡ������ͨ����SelectionKey
				Iterator<SelectionKey> keys = selector.selectedKeys().iterator();
				while(keys.hasNext()){
					SelectionKey key = keys.next();
					keys.remove();
					//  ��֪�˼��Ƿ���Ч��
					if(key.isValid()){
						if(key.isAcceptable()){  //���������¼�  ���Դ˼���ͨ���Ƿ���׼���ý����µ��׽������ӡ�
							System.out.println("����");
							accept(key);   //�����µĿͻ�����
						}
						if(key.isReadable()){
							System.out.println("��д");
							read(key);
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	
	/**
	 * ����ͻ�������   ������Ϊ����ͻ�������һ��Channel Channel��ͻ��˶Խӣ�Channel�󶨵�Selector��
	 * @param key
	 */
	private void accept(SelectionKey key){
		try {
			//��ȡ֮ǰע���SocketChannelͨ��
			ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
			//ִ������������Channel�Ϳͻ��˶Խ�
			SocketChannel sc = ssc.accept();
			//����ģʽΪ������
            sc.configureBlocking(false);
            sc.register(selector,SelectionKey.OP_READ);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	private void read(SelectionKey key){
		try {
			//��ջ������ľ�����
			buffer.clear();
			SocketChannel sc = (SocketChannel)key.channel();
			int count = sc.read(buffer);
			if(count == -1){
				//�رմ�ͨ����
				key.channel().close();
				//����ȡ���˼���ͨ������ѡ������ע��
				key.cancel();
				return;
			}
			//��ȡ�������ݣ���buffer��position��λ��0
            buffer.flip();
            //���ص�ǰλ��������֮���Ԫ������
            byte[] bytes = new byte[buffer.remaining()];
            buffer.get(bytes);
            String body = new String(bytes).trim();
            System.out.println("Server:"+body);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	public static void main(String[] args){
		new Thread(new Server(8379)).start();
	}
	

}
