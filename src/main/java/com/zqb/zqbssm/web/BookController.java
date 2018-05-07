package com.zqb.zqbssm.web;

import com.zqb.zqbssm.dto.AppointExecution;
import com.zqb.zqbssm.dto.Result;
import com.zqb.zqbssm.entity.Book;
import com.zqb.zqbssm.enums.AppointStateEnum;
import com.zqb.zqbssm.exception.NoNumberException;
import com.zqb.zqbssm.exception.RepeatAppointException;
import com.zqb.zqbssm.service.BookService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/book") // url:/模块/资源/{id}/细分 /seckill/list
public class BookController {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private BookService bookService;

//	@RequestMapping(value = "/list", method = RequestMethod.GET)
//	private String list(Model model) {
//		List<Book> list = bookService.getList();
//		model.addAttribute("list", list);
//		// list.jsp + model = ModelAndView
//		return "list";// WEB-INF/jsp/"list".jsp
//	}

	@RequestMapping(value = "/list", method = RequestMethod.GET)
	@ResponseBody
	private Result<List<Book>> list() {
		return new Result<List<Book>>(true, bookService.getList());// WEB-INF/jsp/"list".jsp
	}

//	@RequestMapping(value = "/{bookId}/detail", method = RequestMethod.GET)
//	private String detail(@PathVariable("bookId") Long bookId, Model model) {
//		if (bookId == null) {
//			return "redirect:/book/list";
//		}
//		Book book = bookService.getById(bookId);
//		if (book == null) {
//			return "forward:/book/list";
//		}
//		model.addAttribute("book", book);
//		return "detail";
//	}

	@RequestMapping(value = "/{bookId}/detail", method = RequestMethod.GET)
	@ResponseBody
	private Result<Book> detail(@PathVariable("bookId") Long bookId) {
		return new Result<Book>(true, bookService.getById(bookId));
	}

	//ajax json
	@RequestMapping(value = "/{bookId}/appoint", method = RequestMethod.GET, produces = {
			"application/json; charset=utf-8" })
	@ResponseBody
	private Result<AppointExecution> appoint(@PathVariable("bookId") Long bookId, @RequestParam("studentId") Long studentId) {
		if (studentId == null || studentId.equals("")) {
			return new Result<>(false, "学号不能为空");
		}
		//AppointExecution execution = bookService.appoint(bookId, studentId);//错误写法，不能统一返回，要处理异常（失败）情况
		AppointExecution execution = null;
		try {
			execution = bookService.appoint(bookId, studentId);
		} catch (NoNumberException e1) {
			execution = new AppointExecution(bookId, AppointStateEnum.NO_NUMBER);
		} catch (RepeatAppointException e2) {
			execution = new AppointExecution(bookId, AppointStateEnum.REPEAT_APPOINT);
		} catch (Exception e) {
			execution = new AppointExecution(bookId, AppointStateEnum.INNER_ERROR);
		}
		return new Result<AppointExecution>(true, execution);
	}

}