package com.nio;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class Server implements Runnable{
	//��·������
	private Selector selector;
	/*
	 * allocate(256) ����һ������Ϊ256�ֽڵ�ByteBuffer
	 * ����һ���µ��ֽڻ�����
	 * �»�������λ�ý�Ϊ�㣬����޽�Ϊ�������������ǲ�ȷ���ġ�
	 * �������Ƿ���еײ�ʵ�����飬���Ƕ��ǲ�ȷ����
	 */
	private ByteBuffer buffer = ByteBuffer.allocate(1024);
	
	public Server(int port) {
		try {
			//open()����ѡ������
			selector = Selector.open();
			//ServerSocketChannel ����������������׽��ֵĿ�ѡ��ͨ����
			ServerSocketChannel ssc = ServerSocketChannel.open();
			//���÷�����Ϊ��������ʽ
			ssc.configureBlocking(false);
			ssc.bind(new InetSocketAddress(port));
			//�ѷ�����ͨ��ע�ᵽ��·����ѡ�����ϣ�����������״̬
			ssc.register(selector, SelectionKey.OP_ACCEPT);
			System.out.println("Server start whit port:"+port);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public void run() {
		while(true) {
			try {
				//���ﴦ���¼���Ҳ�������ģ��¼������ͻ������ӣ��ͻ������ݵ��������ͻ��˶Ͽ����ӵȵ�
				//��û���¼�������Ҳ������
				//select() ���������һֱ������ĳ��ע���ͨ�����¼�����
				selector.select();
				System.out.println("��������");
				//���������Ѿ�ע�ᵽ��·��������ͨ����selectionKey
				Iterator<SelectionKey> keys = selector.selectedKeys().iterator();
				while(keys.hasNext()) {
					SelectionKey key = keys.next();
					/*
					 * Selector.select()ȡ���¼����е�ȫ���¼��������ɾ�������´���ѯ��ʱ�򣬵���Selector.select()��ȡ���ɵ��¼���
					 * �������ظ�����
					 */
					keys.remove();
					if(key.isValid()) {  //�ж�key�Ƿ���Ч
						if(key.isAcceptable()) { //���������¼�
							accept(key);  //�����¿ͻ��˵�����
						}
						if(key.isReadable()) {  //�����ݵ���
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
	 * ����ͻ�������
	 * ������Ϊÿ���ͻ�������һ��Channel
	 * Channel��ͻ��˶Խ�
	 * Channel�󶨵�Selector��
	 * @param key
	 */
	private void accept(SelectionKey key) {
		try {
			//��ȡ֮ǰע���SocketChannelͨ��
			ServerSocketChannel ssc = (ServerSocketChannel)key.channel();
			//ִ������������Channel�Ϳͻ��˶Խ�
			SocketChannel sc = ssc.accept();
			//����ģʽΪ������
			sc.configureBlocking(false);
			sc.register(selector, SelectionKey.OP_READ);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	
	private void read(SelectionKey key) {
		try {
			//��ջ������ľ�����
			buffer.clear();
			SocketChannel sc = (SocketChannel)key.channel();
			int count = sc.read(buffer);
			if(count == -1) {
				key.channel().close();
				key.cancel();
				return;
			}
			//��ȡ�������ݣ���buffer��position��λ��0
			/*
			 * ʹ������Ϊһϵ���µ�ͨ��д�����Ի�ȡ ��������׼����������������Ϊ��ǰλ�ã�
			 * Ȼ��λ������Ϊ 0
			 */
            buffer.flip();
            //remaing() ���ص�ǰλ��������֮���Ԫ���������� �˻������е�ʣ��Ԫ����
            byte[] bytes = new byte[buffer.remaining()];
            buffer.get(bytes);
            String body = new String(bytes).trim();
            System.out.println("Server:"+body);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		new Thread(new Server(8379)).start();
	}

}
