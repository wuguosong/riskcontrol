$(function(){
			//��ѯ�ر�&չʾ
			$(".panel-heading").find(".ico-dorp").click(function(){
				$(this).toggleClass("fa-plus");
				$(this).toggleClass("fa-minus");
				$(this).parents(".panel-heading").siblings(".panel-body").stop().slideToggle(300);
			});
			//�˵��ر�&չʾ
			$(".wrap-info .menuico").toggle(function(){
				$(this).addClass("glyphicon-menu-right").removeClass("glyphicon-menu-left");
				$(this).parents(".wrapper").stop().animate({"margin-left":"-150px"},300);
			},function(){
				$(this).addClass("glyphicon-menu-left").removeClass("glyphicon-menu-right");
				$(this).parents(".wrapper").stop().animate({"margin-left":"0"},300);
			});
			//�����˵��ر�&չʾ
			$(".menu-bd .list").children("li").click(function(){
				var dropDown = $(this).find(".sub-list");
				var togClass=$(this).find(".ico");
				$(".menu-bd .list li .sub-list").not(dropDown).slideUp(300);
				$(".menu-bd .list li .ico").not(togClass).removeClass("fa-minus");
				dropDown.slideToggle(300);
				togClass.toggleClass("fa-minus");
			});
			//�ղ�
			$(".ico-wrap .fa-star").click(function(){
				$(this).toggleClass("red-star");
			});
			//�����ɫ����
			$(".allot").click(function(){
				Visa.NoticePopup.show();
			});
			$("#pub-notice .ico-x").click(function(){
				$("#js-index-popup").hide(300);
				$("#_Dialog_maskwrap").hide(100);
			});
		});