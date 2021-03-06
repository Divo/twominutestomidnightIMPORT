#!/usr/bin/env python
# -*- coding: utf-8 -*-
# generated by wxGlade 0.6.3 on Tue Mar  8 18:29:44 2011

import wx

# begin wxGlade: extracode
# end wxGlade



class ProxyGUI(wx.Frame):
    def __init__(self, *args, **kwds):
        # begin wxGlade: ProxyGUI.__init__
        kwds["style"] = wx.DEFAULT_FRAME_STYLE
        wx.Frame.__init__(self, *args, **kwds)
        self.traffic = wx.ListCtrl(self, -1, style=wx.LC_REPORT|wx.SUNKEN_BORDER)
        self.blocklist = wx.ListCtrl(self, -1, style=wx.LC_REPORT|wx.SUNKEN_BORDER)
        self.block = wx.Button(self, -1, "button_1")
        self.panel_1 = wx.Panel(self, -1)
        self.panel_2 = wx.Panel(self, -1)

        self.__set_properties()
        self.__do_layout()
        # end wxGlade

    def __set_properties(self):
        # begin wxGlade: ProxyGUI.__set_properties
        self.SetTitle("frame_1")
        self.SetSize((600, 500))
        # end wxGlade

    def MainLoop():
    	#super(B, self).__init__()
	print 'hello'

    def __do_layout(self):
        # begin wxGlade: ProxyGUI.__do_layout
        sizer_main = wx.BoxSizer(wx.VERTICAL)
        grid_sizer = wx.GridSizer(1, 2, 0, 0)
        sizer2 = wx.BoxSizer(wx.VERTICAL)
        grid_sizer.Add(self.traffic, 1, wx.EXPAND, 0)
        sizer2.Add(self.blocklist, 1, wx.EXPAND, 0)
        sizer2.Add(self.block, 0, 0, 0)
        sizer2.Add(self.panel_1, 1, wx.EXPAND, 0)
        sizer2.Add(self.panel_2, 1, wx.EXPAND, 0)
        grid_sizer.Add(sizer2, 1, wx.EXPAND, 0)
        sizer_main.Add(grid_sizer, 1, wx.EXPAND, 0)
        self.SetSizer(sizer_main)
        self.Layout()
        self.SetSize((600, 500))
        # end wxGlade

# end of class ProxyGUI


if __name__ == "__main__":
    app = wx.PySimpleApp(0)
    wx.InitAllImageHandlers()
    gui = ProxyGUI(None, -1, "")
    app.SetTopWindow(gui)
    gui.Show()
    app.MainLoop()
