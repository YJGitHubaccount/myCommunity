$(function(){
	$("#publishBtn").click(publish);
});

function publish() {
	$("#publishModal").modal("hide");

	// 获取标题和内容
	var title = $("#recipient-name").val();
	var content = $("#message-text").val();

	// 发送异步请求(POST)
	$.post(

		CONTEXT_PATH + "/discuss/add",

		{"title":title,"content":content},

		function(data) {
			data = $.parseJSON(data);

			// 在提示框的文本中显示返回消息
			$("#hintBody").text(data.msg);

			// 显示提示框
			$("#hintModal").modal("show");

			// 2秒后,自动隐藏提示框并刷新
			setTimeout(function(){
				$("#hintModal").modal("hide");

				// 如果发布成功，刷新页面
				if(data.code == 0) {
					window.location.reload();
				}
			}, 2000);
		}
	);

	// $("#hintModal").modal("show");
	// setTimeout(function(){
	// 	$("#hintModal").modal("hide");
	// }, 2000);
}