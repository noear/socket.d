#ifndef ENTITY_METAS_H_
#define ENTITY_METAS_H_

/**
 * 框架版本号
 */
#define META_SOCKETD_VERSION			"Socket.D"

/**
 * 发起端真实IP
 * 
 */
#define META_X_REAL_IP					"X-Real-IP"

/**
 * 数据长度
 */
#define META_DATA_LENGTH				"Data-Length"

/**
 * 数据类型
 */
#define META_DATA_TYPE					"Data-Type"

/**
 * 数据分片索引
 */
#define META_DATA_FRAGMENT_IDX			"Data-Fragment-Idx"

/**
 * 数据分片总数
 */
#define META_DATA_FRAGMENT_TOTAL		"Data-Fragment-Total"

/**
 * 数据描述之文件名
 */
#define META_DATA_DISPOSITION_FILENAME	"Data-Disposition-Filename"

/**
 * 数据范围开始（相当于分页）
 */
#define META_RANGE_START				"Data-Range-Start"

/**
 * 数据范围长度
 */
#define META_RANGE_SIZE					"Data-Range-Size"

#endif