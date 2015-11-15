PullRefreshView  
===================================
  操作过程不引起控件重新计算尺寸和重新布局，只发生重绘 所以滑动过程流畅。合理触摸事件分发过程，减少无关操作的执行 
    
	FlingLayout
-----------------------------------  
		实现了弹性拖动，支持任意控件。
		[![image]](https://raw.githubusercontent.com/Y-bao/PullRefreshView/master/GIF/1.gif)  
 
	PullRefreshLayout
-----------------------------------  
		继承自FlingLayout，具有弹性拖动，配合BaseHeaderView，BaseFooterView 实现经典的 下拉刷新，上拉加载的功能，支持任意控件。不使用BaseHeaderView，BaseFooterView时功能和FlingLayout相同。		
		[![image]](https://raw.githubusercontent.com/Y-bao/PullRefreshView/master/GIF/2.gif)  


	RGPullRefreshLayout
-----------------------------------  
		PullRefreshLayout 在其基础上实现，下拉刷新和上拉加载 滑出方式的选择。
		[![image]](https://raw.githubusercontent.com/Y-bao/PullRefreshView/master/GIF/3.gif) 
		[![image]](https://raw.githubusercontent.com/Y-bao/PullRefreshView/master/GIF/4.gif) 
		[![image]](https://raw.githubusercontent.com/Y-bao/PullRefreshView/master/GIF/5.gif) 
		public final static int LAYOUT_NORMAL = 0x0000;
		public final static int LAYOUT_DRAWER_HEADER = 0x0001;
		public final static int LAYOUT_SCROLLER_HEADER = 0x0010;
		public final static int LAYOUT_DRAWER_FOOTER = 0x0100;
		public final static int LAYOUT_SCROLLER_FOOTER = 0x1000;

		public final static int LAYOUT_SCROLLER = LAYOUT_SCROLLER_HEADER | LAYOUT_SCROLLER_FOOTER;
		public final static int LAYOUT_DRAWER = LAYOUT_DRAWER_HEADER | LAYOUT_DRAWER_FOOTER;

		public final static int LAYOUT_HEADER_MASK = 0x0011;
		public final static int LAYOUT_FOOTER_MASK = 0x1100;
		
		public void setLayoutType(int layoutType);
		
	其它图片展示
-----------------------------------  
		[![image]](https://raw.githubusercontent.com/Y-bao/PullRefreshView/master/GIF/6.gif) 
