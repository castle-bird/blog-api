local currentTokenKey = KEYS[1]
local newTokenKey = KEYS[2]

local currentTokenHash = ARGV[1]
local newTokenHash = ARGV[2]
local newExpiresAtSeconds = ARGV[3]
local sessionKeyPrefix = ARGV[4]
local userSessionsKeyPrefix = ARGV[5]

-- 1. currentTokenKey에서 sessionId를 조회한다.
local sessionId = redis.call('GET', currentTokenKey)

-- 2. sessionId가 없으면 Rotate를 거부한다.
-- → 전달된 Refresh Token이 Redis에 존재하지 않음 = 오래된 or 유효하지 않은 Refresh Token
if not sessionId then
  return nil
end

-- 3. session key에서 userId와 activeTokenHash를 조회한다.
local sessionKey = sessionKeyPrefix .. sessionId
local userId = redis.call('HGET', sessionKey, 'userId')
local activeTokenHash = redis.call('HGET', sessionKey, 'activeTokenHash')

-- 4. 현재 세션 정보가 없거나, 요청 RT가 활성 RT가 아니면 Rotate를 거부한다.
-- → userId & activeTokenHash가 없다: 이전 세션 저장이 잘못 되었거나, 데이터가 삭제된 상태 → 데이터 손상
-- → activeTokenHash ~= currentTokenHash:
--                 요청 RT 해시가 이 세션의 현재 활성 RT 해시와 다르다.
--                 이미 Rotate된 이전 RT 등을 사용한 경우다.
if not userId
    or not activeTokenHash
    or activeTokenHash ~= currentTokenHash then
  return nil
end

-- 5. currentTokenKey를 삭제하고 newTokenKey에 sessionId를 저장한다.
redis.call('DEL', currentTokenKey)
redis.call('SET', newTokenKey, sessionId)

-- 6. session key의 activeTokenHash를 newTokenHash로 교체한다.
redis.call('HSET', sessionKey, 'activeTokenHash', newTokenHash)

-- 7. 사용자 세션 ZSET의 sessionId 만료 시간을 갱신한다.
-- → ZSET은 같은 값으로 ADD하면 기존 값을 덮어쓴다.
local userSessionsKey = userSessionsKeyPrefix .. userId .. ':sessions'
redis.call('ZADD', userSessionsKey, newExpiresAtSeconds, sessionId)

-- 8. newTokenKey, session key, 사용자 세션 ZSET의 TTL을 갱신한다.
redis.call('EXPIREAT', newTokenKey, newExpiresAtSeconds)
redis.call('EXPIREAT', sessionKey, newExpiresAtSeconds)
redis.call('EXPIREAT', userSessionsKey, newExpiresAtSeconds)

-- 9. userId를 반환한다.
return tonumber(userId)
