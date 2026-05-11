const { Builder, By, Key, until } = require('selenium-webdriver');

async function runMegaE2ETest() {
    let driver = await new Builder().forBrowser('chrome').build();
    let baseUrl = 'http://localhost:8080/api';
    const sleep = (ms) => new Promise(resolve => setTimeout(resolve, ms));

    try {
        console.log('🚀 BẮT ĐẦU MEGA E2E TEST - KIỂM THỬ TOÀN BỘ HỆ THỐNG 🚀\n');
        await sleep(2000);

        // --- 1. TEST THANG ĐIỂM (GRADE SCALES) ---
        console.log('>>> MODULE 1: THANG ĐIỂM');
        await driver.get(`${baseUrl}/grade-scales`);
        await sleep(1500);
        await driver.wait(until.elementLocated(By.partialLinkText('Thêm')), 5000).click();
        
        await sleep(1500);
        await driver.wait(until.elementLocated(By.name('scaleCode')), 5000);
        await driver.findElement(By.name('scaleCode')).sendKeys('TEST_S');
        await driver.findElement(By.name('letterGrade')).sendKeys('S');
        await driver.findElement(By.name('minScore')).sendKeys('9.5');
        await driver.findElement(By.name('maxScore')).sendKeys('10.0');
        await driver.findElement(By.name('gpaValue')).sendKeys('4.0');
        await driver.findElement(By.css('button[type="submit"]')).click();
        
        await driver.wait(until.alertIsPresent(), 5000);
        await (await driver.switchTo().alert()).accept();
        await driver.sleep(2000); // Đợi bảng load lại dữ liệu mới
        console.log('✅ Thang điểm: Thêm mới thành công');

        // Sửa Thang điểm (Dùng contains để tránh lỗi khoảng trắng)
        let editScaleBtn = await driver.wait(until.elementLocated(By.xpath("//td[contains(text(), 'TEST_S')]/..//a[contains(@class, 'btn-primary') or contains(@class, 'btn-light')]")), 5000);
        await editScaleBtn.click();
        let letterField = await driver.wait(until.elementLocated(By.name('letterGrade')), 5000);
        await letterField.clear();
        await letterField.sendKeys('S+');
        await driver.findElement(By.css('button[type="submit"]')).click();
        await driver.wait(until.alertIsPresent(), 5000);
        await (await driver.switchTo().alert()).accept();
        console.log('✅ Thang điểm: Cập nhật thành công');


        // --- 2. TEST THÀNH PHẦN ĐIỂM (GRADE COMPONENTS) ---
        console.log('\n>>> MODULE 2: THÀNH PHẦN ĐIỂM');
        await sleep(1500);
        await driver.get(`${baseUrl}/grade-components`);
        await sleep(1500);
        await driver.wait(until.elementLocated(By.partialLinkText('Thêm mới')), 5000).click();
        
        await sleep(1500);
        await driver.wait(until.elementLocated(By.name('componentName')), 5000);
        await driver.findElement(By.name('courseSectionId')).sendKeys('5085c178-a215-47ee-a605-456be5ee786e');
        await driver.findElement(By.name('componentCode')).sendKeys('MEGA_CC');
        await driver.findElement(By.name('componentName')).sendKeys('Chuyên cần Mega');
        await driver.findElement(By.name('weightPercentage')).sendKeys('10');
        await driver.findElement(By.css('button[type="submit"]')).click();
        console.log('✅ Thành phần điểm: Thêm mới thành công');


        // --- 3. TEST ĐIỂM SINH VIÊN (STUDENT GRADES) ---
        console.log('\n>>> MODULE 3: ĐIỂM SINH VIÊN');
        await sleep(1500);
        await driver.get(`${baseUrl}/student-grades`);
        await sleep(1500);
        await driver.wait(until.elementLocated(By.partialLinkText('Thêm')), 5000).click();
        
        await sleep(1500);
        await driver.wait(until.elementLocated(By.name('score')), 5000);
        // Chọn thành phần điểm đầu tiên trong dropdown
        let selectElement = await driver.findElement(By.name('gradeComponentId'));
        await selectElement.click();
        let options = await selectElement.findElements(By.tagName('option'));
        if (options.length > 1) await options[1].click();

        await driver.findElement(By.name('score')).sendKeys('8.5');
        await driver.findElement(By.name('registrationId')).sendKeys('c4ce0058-6505-486a-83ff-025f4a21b348');
        await driver.findElement(By.name('note')).sendKeys('MEGA_TEST_NOTE');
        await driver.findElement(By.css('button[type="submit"]')).click();
        
        await driver.wait(until.urlContains('/api/student-grades'), 5000);
        console.log('✅ Điểm sinh viên: Thêm mới thành công');

        // --- DỌN DẸP DỮ LIỆU TEST ---
        console.log('\n>>> BƯỚC CUỐI: DỌN DẸP DỮ LIỆU TEST');

        // Xóa Điểm sinh viên vừa tạo (Dùng JS Click để tránh lỗi intercepted)
        let delGradeBtn = await driver.wait(until.elementLocated(By.xpath("//td[contains(text(), 'MEGA_TEST_NOTE')]/..//button[contains(@class, 'btn-outline-danger')]")), 5000);
        await driver.executeScript("arguments[0].click();", delGradeBtn);
        await driver.wait(until.alertIsPresent(), 5000);
        await (await driver.switchTo().alert()).accept();
        console.log('✅ Dọn dẹp: Đã xóa Điểm sinh viên test');
        
        // Xóa Thang điểm TEST_S
        await driver.get(`${baseUrl}/grade-scales`);
        let delScaleBtn = await driver.wait(until.elementLocated(By.xpath("//td[contains(text(), 'TEST_S')]/..//button[contains(@class, 'btn-light')]")), 5000);
        await driver.executeScript("arguments[0].click();", delScaleBtn);
        await driver.wait(until.alertIsPresent(), 5000);
        await (await driver.switchTo().alert()).accept();
        console.log('✅ Dọn dẹp: Đã xóa Thang điểm test');

        // Xóa Thành phần điểm MEGA_CC
        await driver.get(`${baseUrl}/grade-components`);
        let delCompBtn = await driver.wait(until.elementLocated(By.xpath("//span[text()='MEGA_CC']/../../..//button[contains(@class, 'btn-outline-danger')]")), 5000);
        await driver.executeScript("arguments[0].click();", delCompBtn);
        await driver.wait(until.alertIsPresent(), 5000);
        await (await driver.switchTo().alert()).accept();
        console.log('✅ Dọn dẹp: Đã xóa Thành phần điểm test');

        console.log('\n🏆 CHÚC MỪNG! HỆ THỐNG ĐÃ VƯỢT QUA BÀI TEST TOÀN DIỆN (MEGA E2E)!');

    } catch (error) {
        console.error('\n❌ LỖI TRONG QUÁ TRÌNH MEGA TEST:', error.message);
    } finally {
        setTimeout(async () => {
            await driver.quit();
            console.log('\n👋 Đã kết thúc phiên làm việc Selenium.');
        }, 3000);
    }
}

runMegaE2ETest();
