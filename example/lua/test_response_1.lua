--д��Ӧͷ  
ngx.header.a = "1"  
--�����Ӧͷ����ʹ��table  
ngx.header.b = {"2", "3"}  
--�����Ӧ  
ngx.say("a", "b", "<br/>")  
ngx.print("c", "d", "<br/>")  
--200״̬���˳�  
return ngx.exit(200)  