local redis = require 'resty.redis'  
local cjson = require 'cjson'  
  
--ȫ�ֱ��������Ƽ�  
count = 1  
  
--����ȫ���ڴ�  
local shared_data = ngx.shared.shared_data  
shared_data:set("count", 2)
shared_data:set("newCount",100)  