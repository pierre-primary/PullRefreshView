PullRefreshView  
===================================
简介：
-----------------------------------
###设计目标：
(两年前写的第一版PullRefreshView 的 目标）<br/>
1.尽可能支持更多的控件(理论上支持所有控件，但除了默认提供的，其他要自行实现Pullable接口)<br/>
2.教少的工作量，代码可读性高，少 甚至不使用其它库。(当时库不多，而且开发过程不像引用太多的库，最终无需其他库，为了支持RecyclerView，才用的V7)。<br/>
3.兼容api 7。（当时2.1的机还很多。最终可支持2.1，当前支持2.2+，稍微修改还是可以支持2.1的)
操作过程不引起控件重新计算尺寸和重新布局，只发生重绘 所以滑动过程流畅。合理触摸事件分发过程，减少无关操作的执行 <br/>
4.流畅<br/>
5.多交互控件嵌套 交互事件不冲突（这样才能叫做通用控件）<br/>
6.scroll和overscroll连贯滑动,scrolling中不用放手的scroll和overscroll连贯（我自己的强迫症）<br/>
###分析：
####针对目标1：
将View分成两种， 自身可滑动 和 自身不可滑动。自身不可滑动的不必考虑冲突问题直接移动就好，自身可滑动要考虑滑动冲突，可横向的通过判断dy，dx大小谁大就行，竖向的只有当滑到顶部才可以下拉，滑到底部才可上拉，为此设计了Pullable接口。
####针对目标2：
灵活运用:<br/>
1.处理手势交互（dispatchTouchEvent()，onInterceptTouchEvent()，onTouchEvent()，MotionEvent）（辅助GestureDetector,VelocityTracker）<br/>
2.视觉（动画，变形）（ondraw()，dispatchDraw()）（scrollTo()，scrollBy()）（辅助Scroller）<br/>
3.确定大小、位置（onMeasure()，onLayout()）（getScrollY()）<br/>
4.Activity和View 的各种生命周期和状态的回调函数<br/>
ps：后来发现以上的1.2.3.4.都是自定义交互控件的关键知识点。3和4 一般用来获取初始数据和设置一些初始化的状态
####针对目标3：
这是其他目标一完成的<br/>
####针对目标4：
同针对目标2<br/>
####针对目标5：
同针对目标2<br/>
####针对目标6：
处理好手势分发，并用MotionEvent控制子View，（Pullable用来获取子View的状态，MotionEvent用来控制子View，实现父子间的交互）

###重构：
目前github上托管的是重构完的，未完 sleep

1.0.1
----------------------------------- 
目前滑出方式的选择改为 使用annotation标记+反射获取实现。
原来通过setLayoutType()  每个使用RGPullRefreshLayout的地方调用时都要调一次相对麻烦，决定改掉它。
开始时不准备用使用annotation标记+反射获取实现，因为效率问题。所以开始是在Loadable和Refreshable两个接口中添加getLayoutType()接口，但是这样一来使用PullRefreshLayout的程序员就要实现Loadable和Refreshable，RGPullRefreshLayout对PullRefreshLayout造成影响，我本意是希望RGPullRefreshLayout继承PullRefreshLayout，但又相对独立。所以才选择使用annotation标记+反射获取实现， 他只会在初始时期调用一次反射获取实现，所以性能上的影响不大。
    
1.0
----------------------------------- 

FlingLayout
-----------------------------------  
实现了弹性拖动，支持任意控件。
	
![](https://raw.githubusercontent.com/Y-bao/PullRefreshView/master/GIF/1.gif) 
 
PullRefreshLayout
-----------------------------------  
继承自FlingLayout，具有弹性拖动，配合BaseHeaderView，BaseFooterView 实现经典的 下拉刷新，上拉加载的功能，支持任意控件。不使用BaseHeaderView，BaseFooterView时功能和FlingLayout相同。	
	
![](https://raw.githubusercontent.com/Y-bao/PullRefreshView/master/GIF/2.gif) 


RGPullRefreshLayout
-----------------------------------  
PullRefreshLayout 在其基础上实现，下拉刷新和上拉加载 滑出方式的选择。
	
![](https://raw.githubusercontent.com/Y-bao/PullRefreshView/master/GIF/3.gif)
![](https://raw.githubusercontent.com/Y-bao/PullRefreshView/master/GIF/4.gif)
![](https://raw.githubusercontent.com/Y-bao/PullRefreshView/master/GIF/5.gif) 

		public final static int LAYOUT_NORMAL = 0x00;
		public final static int LAYOUT_DRAWER = 0x01;
		public final static int LAYOUT_SCROLLER = 0x10;

其它图片展示
-----------------------------------  
![](https://raw.githubusercontent.com/Y-bao/PullRefreshView/master/GIF/6.gif) 
