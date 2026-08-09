local tokenKey = KEYS[1]

local sessionKeyPrefix = ARGV[1]
local userSessionsKeyPrefix = ARGV[2]

-- 1. tokenKey에서 sessionId를 조회한다.
local sessionId = redis.call('GET', tokenKey)

-- 2. sessionId가 없으면 종료한다.
if not sessionId then
  return
end

-- 3. session key에서 userId를 조회한다.
local sessionKey = sessionKeyPrefix .. sessionId
local userId = redis.call('HGET', sessionKey, 'userId')

-- 4. tokenKey와 session key를 삭제한다.
redis.call('DEL', tokenKey)
redis.call('DEL', sessionKey)

-- 5. 사용자 세션 ZSET에서 sessionId를 제거한다.
if userId then
  local userSessionsKey = userSessionsKeyPrefix .. userId .. ':sessions'
  redis.call('ZREM', userSessionsKey, sessionId)

-- 6. 사용자 세션 ZSET이 비었으면 ZSET key를 삭제한다.
  if redis.call('ZCARD', userSessionsKey) == 0 then
    redis.call('DEL', userSessionsKey)
  end
end
