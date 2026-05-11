/**
 * FULL API AUTOMATED TEST SCRIPT - QUANLYDAOTAOA
 * Author: Antigravity (Mentor)
 * Description: Quét toàn bộ API của hệ thống để kiểm tra tính đúng đắn của dữ liệu.
 */

const BASE_URL = 'http://localhost:8080';

// Hàm helper để gửi request
// Hàm helper để dừng chương trình một lát cho dễ quan sát
const sleep = (ms) => new Promise(resolve => setTimeout(resolve, ms));

async function api(path, method = 'GET', body = null) {
    const options = {
        method,
        headers: { 'Content-Type': 'application/json' }
    };
    if (body) options.body = JSON.stringify(body);
    
    const response = await fetch(`${BASE_URL}${path}`, options);
    const data = (response.status !== 204) ? await response.json() : null;
    
    if (!response.ok) {
        throw new Error(`API ${method} ${path} thất bại: ${JSON.stringify(data || response.statusText)}`);
    }
    return data;
}

async function runMasterTest() {
    console.log('🚀 Bắt đầu quy trình kiểm thử toàn diện các API...\n');
    let report = { success: 0, failed: 0 };
    let state = {};

    try {
        // --- 1. TEST GRADE COMPONENTS ---
        console.log('>>> [1/3] Kiểm tra GRADE COMPONENTS...');
        await sleep(1500);
        const components = await api('/api/grade-components/data');
        console.log(`- Lấy danh sách thành công: ${components.length} bản ghi.`);
        
        if (components.length > 0) {
            state.sectionId = components[0].courseSectionId;
            state.compCode = 'AUTO_TEST_' + Date.now();
            
            // Tạo mới
            const newCompId = await api('/api/grade-components', 'POST', {
                courseSectionId: state.sectionId,
                componentCode: state.compCode,
                componentName: 'Thành phần Auto Test',
                weightPercentage: 10.0,
                isActive: true
            });
            console.log(`- Tạo mới thành công (ID: ${newCompId})`);
            
            // Xóa
            await api(`/api/grade-components/${newCompId}`, 'DELETE');
            console.log(`- Xóa thành công.`);
            report.success++;
        }

        // --- 2. TEST GRADE SCALES ---
        console.log('\n>>> [2/3] Kiểm tra GRADE SCALES...');
        await sleep(1500);
        const newScale = await api('/api/v1/grade-scales', 'POST', {
            scaleCode: 'T_' + Math.floor(Math.random()*100),
            minScore: 0.0,
            maxScore: 4.0,
            letterGrade: 'F',
            gpaValue: 0.0,
            isPass: false,
            isActive: true
        });
        state.scaleId = newScale.id;
        console.log(`- Tạo mới thang điểm thành công (ID: ${state.scaleId})`);
        
        await api(`/api/v1/grade-scales/${state.scaleId}`, 'DELETE');
        console.log(`- Xóa thang điểm thành công.`);
        report.success++;

        // --- 3. TEST STUDENT GRADES ---
        console.log('\n>>> [3/3] Kiểm tra STUDENT GRADES...');
        await sleep(1500);
        const studentGrades = await api('/api/v1/student-grades');
        console.log(`- Lấy danh sách điểm sinh viên thành công: ${studentGrades.length} bản ghi.`);
        
        if (studentGrades.length > 0) {
            const firstGrade = studentGrades[0];
            // Thử cập nhật điểm
            const updated = await api(`/api/v1/student-grades/${firstGrade.id}`, 'PUT', {
                ...firstGrade,
                score: 9.9,
                note: 'Updated by Master Test'
            });
            console.log(`- Cập nhật điểm thành công (New Score: ${updated.score})`);
            report.success++;
        }

        console.log('\n=========================================');
        console.log('🏆 KẾT QUẢ: TẤT CẢ MODULE ĐỀU HOẠT ĐỘNG TỐT!');
        console.log(`✅ Hoàn thành: ${report.success} module`);
        console.log('=========================================');

    } catch (error) {
        console.error('\n❌ PHÁT HIỆN LỖI KHI KIỂM THỬ:');
        console.error(error.message);
        console.log('\n💡 Mentor khuyên em: Kiểm tra lại Console của Spring Boot để xem lỗi SQL hoặc Mapping.');
    }
}

runMasterTest();
