local tokenKey = KEYS[1]
local sessionKey = KEYS[2]
local userSessionsKey = KEYS[3]

local sessionId = ARGV[1]
local userId = ARGV[2]
local tokenHash = ARGV[3]
local expiresAtSeconds = ARGV[4]
local maxSessionCount = ARGV[5]
local tokenKeyPrefix = ARGV[6]
local sessionKeyPrefix = ARGV[7]

-- 1. 만료된 사용자 세션 ZSET 항목을 제거한다.
local nowSeconds = tonumber(redis.call('TIME')[1])

redis.call('ZREMRANGEBYSCORE', userSessionsKey, '-inf', nowSeconds)

-- 2. 토큰/세션/사용자 세션 모두 생성 + 저장
redis.call('SET', tokenKey, sessionId)
redis.call('HSET', sessionKey,
  'userId', userId,
  'activeTokenHash', tokenHash
)
redis.call('ZADD', userSessionsKey, expiresAtSeconds, sessionId)

-- 3. 각각 TTL 생성
redis.call('EXPIREAT', tokenKey, expiresAtSeconds)
redis.call('EXPIREAT', sessionKey, expiresAtSeconds)
redis.call('EXPIREAT', userSessionsKey, expiresAtSeconds)

-- 4. ZSET에 세션 저장 후 최대 개수를 넘으면 score가 가장 작은 세션부터 삭제한다.
local sessionCount = redis.call('ZCARD', userSessionsKey)
local excessSessionCount = sessionCount - tonumber(maxSessionCount)

if excessSessionCount > 0 then
  local expiredSessionIds = redis.call(
    'ZRANGE', userSessionsKey, 0, excessSessionCount - 1
  )

  -- ZRANGE는 member만 반환한다.
  for _, expiredSessionId in ipairs(expiredSessionIds) do
    local expiredSessionKey = sessionKeyPrefix .. expiredSessionId
    local expiredTokenHash = redis.call(
      'HGET', expiredSessionKey, 'activeTokenHash'
    )

    -- 토큰 → 세션 → 유저 세션 목록 순으로 제거한다.
    if expiredTokenHash then
      redis.call('DEL', tokenKeyPrefix .. expiredTokenHash)
    end

    redis.call('DEL', expiredSessionKey)
    redis.call('ZREM', userSessionsKey, expiredSessionId)
  end
end
