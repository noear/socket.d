<template>
	<div>
		<!-- Table 1 -->

		<view style="text-align: center">
			<h2>socket.d js uniapp demo</h2>
		</view>
		<hr />
		<form class="form">
			<view class="uni-form-item uni-column">
				<view class="title">ServerUrl :</view>
				<view>
					<input class="longInput" v-model="serverUrl" />
				</view>
			</view>
			<view class="uni-form-item uni-column">
				<view class="title">Input:</view>
				<view>
					<input class="longInput" v-model="inputValue" />
				</view>
			</view>
			<!-- <view class="uploadBox">
				<view class="">
					<button class="upload" @click="select">选择上传文件</button>
				</view>
				<view class="uploadBox-item" v-if="!fileData">
					{{ '未选择任何文件' }}
				</view>
			</view> -->
			<view class="uni-form-item uni-column">
				<view class="title">Message:</view>
				<view class="manyRows">
					<uni-easyinput type="textarea" v-model="message" placeholder="请输入内容"></uni-easyinput>
				</view>
			</view>
			<view class="content">
				<button class="btn" @click="openBtn">{{ openBtnText }}</button>
				<button class="btn" @click="send">发送</button>
				<button class="btn" @click="sendAndRequest">发送并请求</button>
				<button class="btn" @click="sendAndSubscribe">
					发送并订阅
				</button>
				<button class="btn" @click="push">接收推送</button>
				<button class="btn" @click="unpush">取消推送</button>
				<!-- <button class="btn" @click="uploadFile">上传文件</button> -->
				<button class="btn" @click="downloadFile">下载文件</button>
				<button class="btn" @click="uploadData">提交大文本</button>
			</view>
		</form>
	</div>
</template>

<script>
import { SocketD } from '@noear/socket.d';
export default {
	data() {
		return {
			fileValue: undefined,
			serverUrl: 'sd:ws://127.0.0.1:8602/?u=a&p=2',
			inputValue: 'hello',
			file: null,
			isOpen: false,
			message: '',
			clientSession: undefined,
			openBtnText: '连接',
			fileData: undefined
		};
	},
	methods: {
		select() {
			const _this = this
			// 选择要上传的文件
			uni.chooseImage({
				count: 1, // 最多只能选择一张图片
				success(res) {
					_this.fileData = res.tempFiles[0]; // 获取临时路径
				}
			});
		},
		async open(callback) {
			if (!this.serverUrl) {
				alert('serverUrl不能为空!');
				return;
			}
			this.clientSession = await SocketD.createClient(
				this.serverUrl.trim()
			)
				.config((c) => c.fragmentSize(1024 * 1024))
				.listen(
					SocketD.newEventListener().doOnMessage((s, m) => {
						this.appendToMessageList('收到推送', m.dataAsString());
					})
				)
				.open();
			console.log('session', this.clientSession);
			if (callback) callback();
		},
		close(callback) {
			this.clientSession.close();
			if (callback) callback();
		},
		send(type) {
			if (!this.inputValue) {
				alert('输入消息不能为空!');
				return;
			}

			if (type == 1) {
				this.appendToMessageList('发送并请求', this.inputValue);
				this.clientSession
					.sendAndRequest('/demo', SocketD.newEntity(this.inputValue))
					.thenReply((reply) => {
						console.log('reply', reply);
						this.appendToMessageList('答复', reply.dataAsString());
					});
			} else if (type == 2) {
				this.appendToMessageList('发送并订阅', this.inputValue);
				this.clientSession
					.sendAndSubscribe(
						'/demo',
						SocketD.newEntity(this.inputValue).metaPut(
							SocketD.EntityMetas.META_RANGE_SIZE,
							'3'
						)
					)
					.thenReply((reply) => {
						console.log('reply', reply);
						if (reply.isEnd()) {
							this.appendToMessageList(
								'答复结束',
								reply.dataAsString()
							);
						} else {
							this.appendToMessageList(
								'答复',
								reply.dataAsString()
							);
						}
					});
			} else {
				this.appendToMessageList('发送', this.inputValue);
				this.clientSession.send(
					'/demo',
					SocketD.newEntity(this.inputValue)
				);
			}
		},
		appendToMessageList(hint, msg) {
			this.message =
				`[${this.dateFormat(
					new Date(),
					'yyyy-MM-dd hh:mm:ss.SSS'
				)}] ${hint}：${msg}\n` + this.message;
		},
		openBtn() {
			if (this.isOpen) {
				this.close(() => {
					this.openBtnText = '连接';
					this.isOpen = false;
				});
			} else {
				this.open(() => {
					this.openBtnText = '关闭';
					this.isOpen = true;
				});
			}
		},
		sendBtn() {
			if (this.isOpen) {
				this.send(0);
			}
		},
		sendAndRequest() {
			if (this.isOpen) {
				this.send(1);
			}
		},
		sendAndSubscribe() {
			if (this.isOpen) {
				this.send(2);
			}
		},
		push() {
			if (this.isOpen) {
				this.clientSession.send('/push', SocketD.newEntity());
			}
		},
		unpush() {
			if (this.isOpen) {
				this.clientSession.send('/unpush', SocketD.newEntity());
			}
		},
		uploadFile() {
			if (this.isOpen) {
				if (!this.fileData) {
					alert('请选择文件');
					return;
				}

				this.appendToMessageList('发送文件并请求', this.fileData.name);
				debugger
				this.clientSession
					.sendAndRequest('/upload', SocketD.newEntity(this.fileData))
					.thenReply((reply) => {
						console.log('reply', reply);
						this.appendToMessageList('答复', reply.dataAsString());
					})
					.thenProgress((isSend, val, max) => {
						if (isSend) {
							this.appendToMessageList(
								'发送进度',
								val + '/' + max
							);
						}
					});
			}
		},
		downloadFile() {
			if (this.isOpen) {
				this.appendToMessageList('下载文件', '...');
				this.clientSession
					.sendAndRequest('/download', SocketD.newEntity())
					.thenReply((reply) => {
						console.log('reply', reply);

						const fileName = reply.meta(
							SocketD.EntityMetas.META_DATA_DISPOSITION_FILENAME
						);
						if (fileName) {
							this.appendToMessageList(
								'下载文件',
								'file=' +
								fileName +
								', size=' +
								reply.dataSize()
							);
						} else {
							this.appendToMessageList(
								'下载文件',
								'没有收到文件:('
							);
						}
					})
					.thenProgress((isSend, val, max) => {
						if (!isSend) {
							this.appendToMessageList(
								'下载进度',
								val + '/' + max
							);
						}
					});
			}
		},
		uploadData() {
			if (this.isOpen) {
				const strSize = 1024 * 1024 * 10;
				let str = '';
				while (str.length < strSize) {
					str += '1234567890';
				}

				this.appendToMessageList('提交大文本块10M', '...');
				this.clientSession
					.sendAndRequest('/upload', SocketD.newEntity(str))
					.thenReply((reply) => {
						console.log('reply', reply);
						this.appendToMessageList('答复', reply.dataAsString());
					})
					.thenProgress((isSend, val, max) => {
						if (isSend) {
							this.appendToMessageList(
								'提交进度',
								val + '/' + max
							);
						}
					});
			}
		},
		dateFormat(date, fmt) {
			// 默认格式
			fmt = fmt ? fmt : 'yyyy-MM-dd hh:mm:ss';

			var o = {
				'M+': date.getMonth() + 1, // 月份
				'd+': date.getDate(), // 日
				'h+': date.getHours(), // 小时
				'm+': date.getMinutes(), // 分
				's+': date.getSeconds(), // 秒
				'q+': Math.floor((date.getMonth() + 3) / 3), // 季度
				'S+': date.getMilliseconds() // 毫秒
			};
			if (/(y+)/.test(fmt)) {
				fmt = fmt.replace(
					RegExp.$1,
					(date.getFullYear() + '').substr(4 - RegExp.$1.length)
				);
			}
			for (var k in o) {
				if (new RegExp('(' + k + ')').test(fmt)) {
					fmt = fmt.replace(
						RegExp.$1,
						RegExp.$1.length == 1
							? o[k]
							: RegExp.$1.length == 2
								? ('00' + o[k]).substr(('' + o[k]).length)
								: ('000' + o[k]).substr(('' + o[k]).length)
					);
				}
			}
			return fmt;
		}
	}
};
</script>
<style>
.uni-form-item .title {
	padding: 20rpx 0;
	display: flex;
	padding-left: 20upx;
}

.form {
	text-align: center;
	margin: 20rpx 0;
	width: 80vw;
}

.title {
	display: inline-block;
	font-weight: bold;
}

.longInput {
	margin-left: 20upx;
	margin-right: 20upx;
	border: 1px solid #eee;
	height: 60upx;
}

.btn {
	/* width: 200upx; */
	width: 46%;
	margin: 0 2%;
	margin-bottom: 10px;
}

.content {
	display: flex;
	/* justify-content: space-between; */
	flex-wrap: wrap;
	margin: 0 -0.5%;
	padding: 20upx;

	/* width: 200upx; */
}

.upload {
	margin-top: 20upx;
	font-size: 20upx;
	width: 250upx;
	height: 60upx;
}

.uploadBox {
	margin-top: 20upx;
	display: flex;
	align-items: center;
	height: 60upx;
	margin-left: 20upx;
}

.uploadBox-item {
	margin-top: 20upx;
	margin-left: 20upx;
	height: 60upx;
	line-height: 60upx;
}

.manyRows {
	margin-left: 20upx;
	margin-right: 20upx;
}
</style>
