--1����ȡȫ�ֹ����ڴ����  
local shared_data = ngx.shared.shared_data  
  
--2����ȡ�ֵ�ֵ  
local i = shared_data:get("i")  
if not i then  
    i = 1  
    --3�����Ը�ֵ  
    shared_data:set("i", i)  
    ngx.say("lazy set i ", i, "<br/>")  
end  
--����  
i = shared_data:incr("i", 1)  
ngx.say("now i=", i, "<br/>")  