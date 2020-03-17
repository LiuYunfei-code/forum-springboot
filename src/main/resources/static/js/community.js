/**
 * 提交问题的评论
 */
function questionComment() {

    var questionId = $(" #parentId ").val();
    var content = $(" #comment-content ").val();
    postComment(questionId, 1, content)

}

/**
 * 提交评论的评论
 */
function comComment(e) {

    var commentId = e.getAttribute("data-id");
    var content = $(" #input-" + commentId).val();
    // debugger;
    postComment(commentId, 2, content);


}


/**
 * 上传评论
 * @param parentId
 * @param type
 * @param content
 */
function postComment(parentId, type, content) {
    if (!content) {
        alert("请输入评论内容！");
        return;
    }
    $.ajax({
        type: "POST",
        url: "/comment",
        contentType: "application/json",
        data: JSON.stringify({
            "parentId": parentId,
            "type": type,
            "content": content
        }),
        success: function (response) {
            console.log(response);
            if (response.code == 200) {
                window.location.reload();
                // alert(response.message);

                // debugger;
                $(" #comment-content ").val("");
                // $(" #sub-comment-content ").val("");
                // appendToTarget(parentId, type)
            } else {
                if (response.code == 2003) {
                    var isAccepted = confirm(response.message);
                    if (isAccepted) {
                        window.open("https://github.com/login/oauth/authorize?client_id=8916c34a3a59a70315b5&redirect_uri=http://localhost:8080/callback&scope=user&state=1");
                        window.localStorage.setItem("closable", true);
                    }
                } else {
                    alert(response.alert);
                }

            }
        },
        dataType: "json"
    });

}

// function collapseComments(e) {
//     var id = e.getAttribute("data-id");
//     var comments = $("#comment-" + id);
//     // 展开和折叠二级评论
//     comments.toggleClass("in");
//     if (comments.hasClass("in")) {
//         e.classList.add("active");
//         $.getJSON("/comment/" + id, function (data) {
//             var commentBody = $("comment-body-" + id);
//             var items = [];
//             $.each(data.data, function (comment) {
//                 var c=$("<div/>",{
//                     "class":"col-md-12 col-xs-12 col-sm-12 sub-comments",
//                     html:comment.content
//                 })
//                 items.push(c);
//
//             });
//             $("<div/>",{
//                 "class":"col-md-12 col-xs-12 col-sm-12 sub-comments collapse",
//                 "id":"comment-"+id,
//                 html:items.join("")
//
//             }).appendTo(commentBody);
//
//             console.log(data)
//         });
//     } else {
//         e.classList.remove("active");
//     }
// }

/**
 * 展开二级评论
 */
function collapseComments(e) {
    var id = e.getAttribute("data-id");
    var comments = $("#comment-" + id);
    // debugger;
    // 获取一下二级评论的展开状态
    var collapse = e.getAttribute("data-collapse");
    if (collapse) {
        // 折叠二级评论
        comments.removeClass("in");
        e.removeAttribute("data-collapse");
        e.classList.remove("active");
    } else {
        var subCommentContainer = $("#comment-" + id);
        if (subCommentContainer.children().length != 1) {
            //展开二级评论
            comments.addClass("in");
            // 标记二级评论展开状态
            e.setAttribute("data-collapse", "in");
            e.classList.add("active");
        } else {
            $.getJSON("/comment/" + id, function (data) {
                console.log(data)
                $.each(data.data.reverse(), function (index, comment) {
                    var mediaLeftElement = $("<div/>", {
                        "class": "media-left"
                    }).append($("<img/>", {
                        "class": "media-object img-rounded",
                        "src": comment.user.avatarUrl
                    }));

                    var mediaBodyElement = $("<div/>", {
                        "class": "media-body"
                    }).append($("<h5/>", {
                        "class": "sub-comment-media-heading",
                        "html": comment.user.name
                    })).append($("<div/>", {
                        "html": comment.content
                    })).append($("<div/>", {
                        "class": "sub-comment-menu"
                    }).append($("<span/>", {
                        "class": "pull-right",
                        "html": moment(comment.gmtCreate).format('YYYY-MM-DD')
                    })));

                    var mediaElement = $("<div/>", {
                        "class": "media"
                    }).append(mediaLeftElement).append(mediaBodyElement).append($("<hr/>"));

                    var commentElement = $("<div/>", {
                        "class": "col-lg-12 col-md-12 col-sm-12 col-xs-12 comments"
                    }).append(mediaElement);

                    subCommentContainer.prepend(commentElement);
                });
                //展开二级评论
                comments.addClass("in");
                // 标记二级评论展开状态
                e.setAttribute("data-collapse", "in");
                e.classList.add("active");
            });
        }
    }
}


/**
 * 回复后追击评论到页面
 */
function appendToTarget(commentId, type) {
    // if (type == 2) {
        $.getJSON("/newComment/" + commentId, function (comment) {

            console.log(comment)
            var mediaLeftElement = $("<div/>", {
                "class": "media-left"
            }).append($("<img/>", {
                "class": "media-object img-rounded",
                // "src": comment.user.
            }));

            var mediaBodyElement = $("<div/>", {
                "class": "media-body"
            }).append($("<h5/>", {
                "class": "sub-comment-media-heading",
                "html": comment.user.name
            })).append($("<div/>", {
                "html": comment.content
            })).append($("<div/>", {
                "class": "sub-comment-menu"
            }).append($("<span/>", {
                "class": "pull-right",
                "html": moment(comment.gmtCreate).format('YYYY-MM-DD')
            })));

            var mediaElement = $("<div/>", {
                "class": "media"
            }).append(mediaLeftElement).append(mediaBodyElement).append($("<hr/>"));

            var commentElement = $("<div/>", {
                "class": "col-lg-12 col-md-12 col-sm-12 col-xs-12 comments"
            }).append(mediaElement);

            $("#sub-comments").prepend(commentElement);
        })
    // }
}